import java.util.*;

public class BuscaInformada {

    // ===== Estrutura da Aresta =====
    static class Aresta {
        String destino;
        double custo;

        Aresta(String destino, double custo) {
            this.destino = destino;
            this.custo = custo;
        }
    }

    // ===== Estrutura do Nó =====
    static class No {
        String nome;
        double g;
        double h;
        No pai;

        No(String nome, double g, double h, No pai) {
            this.nome = nome;
            this.g = g;
            this.h = h;
            this.pai = pai;
        }
    }

    static Map<String, List<Aresta>> grafo = new HashMap<>();
    static Map<String, Double> heuristica = new HashMap<>();

    public static void main(String[] args) {

        construirGrafo();

        String inicio = "A";
        String objetivo = "G";

        gulosa(inicio, objetivo);
        aStar(inicio, objetivo);
        ucs(inicio, objetivo);
    }

    // ===== Construção do Grafo =====
    static void construirGrafo() {

        grafo.put("A", Arrays.asList(
                new Aresta("B", 10),
                new Aresta("C", 1)
        ));

        grafo.put("B", Arrays.asList(
                new Aresta("G", 0)
        ));

        grafo.put("C", Arrays.asList(
                new Aresta("D", 1)
        ));

        grafo.put("D", Arrays.asList(
                new Aresta("E", 1)
        ));

        grafo.put("E", Arrays.asList(
                new Aresta("G", 1)
        ));

        grafo.put("G", new ArrayList<>());

        heuristica.put("A", 3.0);
        heuristica.put("B", 1.0);
        heuristica.put("C", 3.0);
        heuristica.put("D", 2.0);
        heuristica.put("E", 1.0);
        heuristica.put("G", 0.0);
    }

    // ===== Impressão da Fronteira =====
    static void imprimirFronteira(PriorityQueue<No> fila, String tipo) {
        System.out.println("Fronteira atual:");
        for (No n : fila) {
            if (tipo.equals("GULOSA"))
                System.out.println("  " + n.nome + " (h=" + n.h + ")");
            else if (tipo.equals("ASTAR"))
                System.out.println("  " + n.nome + " (g=" + n.g + ", h=" + n.h + ", f=" + (n.g + n.h) + ")");
            else
                System.out.println("  " + n.nome + " (g=" + n.g + ")");
        }
    }

    // ===== Reconstrução de Caminho =====
    static List<String> reconstruirCaminho(No no) {
        List<String> caminho = new ArrayList<>();
        while (no != null) {
            caminho.add(no.nome);
            no = no.pai;
        }
        Collections.reverse(caminho);
        return caminho;
    }

    // ===== BUSCA GULOSA DETALHADA =====
    static void gulosa(String inicio, String objetivo) {

        System.out.println("\n================ GULOSA ================\n");

        PriorityQueue<No> fila = new PriorityQueue<>(Comparator.comparingDouble(n -> n.h));
        fila.add(new No(inicio, 0, heuristica.get(inicio), null));

        int expandidos = 0;

        while (!fila.isEmpty()) {

            imprimirFronteira(fila, "GULOSA");

            No atual = fila.poll();
            expandidos++;

            System.out.println("\nExpandindo: " + atual.nome);
            System.out.println("h(n) = " + atual.h);
            System.out.println("Caminho atual: " + reconstruirCaminho(atual));

            if (atual.nome.equals(objetivo)) {
                System.out.println("\n>>> OBJETIVO ENCONTRADO!");
                imprimirResultado(atual, expandidos);
                return;
            }

            for (Aresta a : grafo.get(atual.nome)) {
                No novo = new No(a.destino, 0, heuristica.get(a.destino), atual);
                System.out.println("  Gerando: " + novo.nome + " (h=" + novo.h + ")");
                fila.add(novo);
            }

            System.out.println("-----------------------------------");
        }
    }

    // ===== A* DETALHADO =====
    static void aStar(String inicio, String objetivo) {

        System.out.println("\n================ A* ================\n");

        PriorityQueue<No> fila = new PriorityQueue<>(
                Comparator.comparingDouble(n -> n.g + n.h)
        );

        fila.add(new No(inicio, 0, heuristica.get(inicio), null));

        int expandidos = 0;

        while (!fila.isEmpty()) {

            imprimirFronteira(fila, "ASTAR");

            No atual = fila.poll();
            expandidos++;

            System.out.println("\nExpandindo: " + atual.nome);
            System.out.println("g(n) = " + atual.g + " | h(n) = " + atual.h + " | f(n) = " + (atual.g + atual.h));
            System.out.println("Caminho atual: " + reconstruirCaminho(atual));

            if (atual.nome.equals(objetivo)) {
                System.out.println("\n>>> OBJETIVO ENCONTRADO!");
                imprimirResultado(atual, expandidos);
                return;
            }

            for (Aresta a : grafo.get(atual.nome)) {
                double novoG = atual.g + a.custo;
                No novo = new No(a.destino, novoG, heuristica.get(a.destino), atual);

                System.out.println("  Gerando: " + novo.nome +
                        " | custo aresta=" + a.custo +
                        " | g=" + novoG +
                        " | h=" + novo.h +
                        " | f=" + (novoG + novo.h));

                fila.add(novo);
            }

            System.out.println("-----------------------------------");
        }
    }

    // ===== UCS DETALHADO =====
    static void ucs(String inicio, String objetivo) {

        System.out.println("\n================ UCS ================\n");

        PriorityQueue<No> fila = new PriorityQueue<>(
                Comparator.comparingDouble(n -> n.g)
        );

        fila.add(new No(inicio, 0, 0, null));

        int expandidos = 0;

        while (!fila.isEmpty()) {

            imprimirFronteira(fila, "UCS");

            No atual = fila.poll();
            expandidos++;

            System.out.println("\nExpandindo: " + atual.nome);
            System.out.println("g(n) = " + atual.g);
            System.out.println("Caminho atual: " + reconstruirCaminho(atual));

            if (atual.nome.equals(objetivo)) {
                System.out.println("\n>>> OBJETIVO ENCONTRADO!");
                imprimirResultado(atual, expandidos);
                return;
            }

            for (Aresta a : grafo.get(atual.nome)) {
                double novoG = atual.g + a.custo;

                No novo = new No(a.destino, novoG, 0, atual);

                System.out.println("  Gerando: " + novo.nome +
                        " | custo aresta=" + a.custo +
                        " | g acumulado=" + novoG);

                fila.add(novo);
            }

            System.out.println("-----------------------------------");
        }
    }

    // ===== Resultado Final =====
    static void imprimirResultado(No no, int expandidos) {

        List<String> caminho = reconstruirCaminho(no);

        System.out.println("\n===== RESULTADO FINAL =====");
        System.out.println("Caminho: " + caminho);
        System.out.println("Custo total: " + no.g);
        System.out.println("Nós expandidos: " + expandidos);
        System.out.println("===========================\n");
    }
}