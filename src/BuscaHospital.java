import java.util.*;

public class BuscaHospital {

    // ===== Classe de Resultado =====
    static class Resultado {
        List<String> caminho;
        int profundidade;
        int nosExpandidos;
        int maxFronteira;

        Resultado(List<String> caminho, int profundidade, int nosExpandidos, int maxFronteira) {
            this.caminho = caminho;
            this.profundidade = profundidade;
            this.nosExpandidos = nosExpandidos;
            this.maxFronteira = maxFronteira;
        }

        void imprimir() {
            System.out.println("\n===== RESULTADO FINAL =====");
            System.out.println("Caminho encontrado: " + caminho);
            System.out.println("Profundidade: " + profundidade);
            System.out.println("Nós expandidos: " + nosExpandidos);
            System.out.println("Máx. fronteira: " + maxFronteira);
            System.out.println("===========================\n");
        }
    }

    // ===== Grafo =====
    static Map<String, List<String>> grafo = new HashMap<>();

    public static void main(String[] args) {

        // Montando mapa exemplo
        grafo.put("A", Arrays.asList("B", "C"));
        grafo.put("B", Arrays.asList("D", "E"));
        grafo.put("C", Arrays.asList("F"));
        grafo.put("D", new ArrayList<>());
        grafo.put("E", Arrays.asList("G", "H"));
        grafo.put("F", new ArrayList<>());
        grafo.put("G", new ArrayList<>());
        grafo.put("H", new ArrayList<>());

        String inicio = "A";
        String objetivo = "H";

        // Executando algoritmos
        bfs(inicio, objetivo).imprimir();
        dfs(inicio, objetivo).imprimir();
        dlsWrapper(inicio, objetivo, 3).imprimir();
        iddfs(inicio, objetivo, 10).imprimir();
    }

    // ===== BFS DETALHADO =====
    static Resultado bfs(String inicio, String objetivo) {

        System.out.println("\n========== BFS ==========");

        Queue<List<String>> fila = new LinkedList<>();
        Set<String> visitados = new HashSet<>();

        fila.add(Arrays.asList(inicio));

        int nosExpandidos = 0;
        int maxFronteira = 1;

        while (!fila.isEmpty()) {

            System.out.println("\nFronteira atual: " + fila);

            List<String> caminho = fila.poll();
            String no = caminho.get(caminho.size() - 1);

            System.out.println("Expandindo nó: " + no);
            System.out.println("Caminho: " + caminho);

            nosExpandidos++;

            if (no.equals(objetivo)) {
                System.out.println(">>> OBJETIVO ENCONTRADO!");
                return new Resultado(caminho, caminho.size() - 1, nosExpandidos, maxFronteira);
            }

            visitados.add(no);

            for (String vizinho : grafo.get(no)) {
                if (!visitados.contains(vizinho)) {
                    List<String> novo = new ArrayList<>(caminho);
                    novo.add(vizinho);

                    System.out.println("  + Adicionando: " + novo);
                    fila.add(novo);
                } else {
                    System.out.println("  - Ignorado: " + vizinho);
                }
            }

            maxFronteira = Math.max(maxFronteira, fila.size());
        }

        return null;
    }

    // ===== DFS DETALHADO =====
    static Resultado dfs(String inicio, String objetivo) {

        System.out.println("\n========== DFS ==========");

        Stack<List<String>> pilha = new Stack<>();
        Set<String> visitados = new HashSet<>();

        pilha.push(Arrays.asList(inicio));

        int nosExpandidos = 0;
        int maxFronteira = 1;

        while (!pilha.isEmpty()) {

            System.out.println("\nPilha atual: " + pilha);

            List<String> caminho = pilha.pop();
            String no = caminho.get(caminho.size() - 1);

            System.out.println("Expandindo nó: " + no);
            System.out.println("Caminho: " + caminho);

            nosExpandidos++;

            if (no.equals(objetivo)) {
                System.out.println(">>> OBJETIVO ENCONTRADO!");
                return new Resultado(caminho, caminho.size() - 1, nosExpandidos, maxFronteira);
            }

            if (!visitados.contains(no)) {
                visitados.add(no);

                List<String> vizinhos = new ArrayList<>(grafo.get(no));
                Collections.reverse(vizinhos);

                for (String vizinho : vizinhos) {
                    List<String> novo = new ArrayList<>(caminho);
                    novo.add(vizinho);

                    System.out.println("  + Empilhando: " + novo);
                    pilha.push(novo);
                }
            }

            maxFronteira = Math.max(maxFronteira, pilha.size());
        }

        return null;
    }

    // ===== DLS WRAPPER =====
    static Resultado dlsWrapper(String inicio, String objetivo, int limite) {

        System.out.println("\n========== DLS (limite = " + limite + ") ==========");

        int[] nosExpandidos = {0};

        List<String> resultado = dls(
                inicio, objetivo, limite,
                new ArrayList<>(List.of(inicio)),
                0,
                nosExpandidos
        );

        if (resultado != null) {
            return new Resultado(resultado, resultado.size() - 1, nosExpandidos[0], -1);
        }

        return null;
    }

    // ===== DLS RECURSIVO DETALHADO =====
    static List<String> dls(String no, String objetivo, int limite,
                            List<String> caminho, int nivel, int[] nosExpandidos) {

        nosExpandidos[0]++;

        System.out.println("Nível " + nivel + " | Nó: " + no + " | Caminho: " + caminho);

        if (no.equals(objetivo)) {
            System.out.println(">>> OBJETIVO ENCONTRADO!");
            return caminho;
        }

        if (limite == 0) {
            System.out.println("!!! Limite atingido em " + no);
            return null;
        }

        for (String vizinho : grafo.get(no)) {

            System.out.println("  -> Explorando: " + vizinho);

            List<String> novo = new ArrayList<>(caminho);
            novo.add(vizinho);

            List<String> resultado = dls(
                    vizinho, objetivo, limite - 1,
                    novo, nivel + 1, nosExpandidos
            );

            if (resultado != null) {
                return resultado;
            }
        }

        System.out.println("  <- Backtrack de: " + no);
        return null;
    }

    // ===== IDDFS DETALHADO =====
    static Resultado iddfs(String inicio, String objetivo, int maxLimite) {

        System.out.println("\n========== IDDFS ==========");

        for (int limite = 0; limite <= maxLimite; limite++) {

            System.out.println("\n--- Iteração com limite: " + limite + " ---");

            int[] nosExpandidos = {0};

            List<String> resultado = dls(
                    inicio, objetivo, limite,
                    new ArrayList<>(List.of(inicio)),
                    0,
                    nosExpandidos
            );

            if (resultado != null) {
                return new Resultado(resultado, resultado.size() - 1, nosExpandidos[0], -1);
            }
        }

        return null;
    }
}