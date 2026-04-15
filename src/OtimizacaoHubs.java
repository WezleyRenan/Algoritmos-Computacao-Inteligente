import java.util.*;

public class OtimizacaoHubs {

    static Random rand = new Random();

    static final int NUM_LOCAIS = 12;
    static final int MAX_HUBS = 4;

    static final int POP_SIZE = 30;
    static final int GERACOES = 50;

    static final int PARTICULAS = 20;
    static final int ITERACOES_PSO = 50;

    static final int EXECUCOES = 5;

    // ===== INDIVÍDUO (GA) =====
    static class Individuo {
        int[] hubs; // 0 ou 1
        double fitness;

        Individuo() {
            hubs = new int[NUM_LOCAIS];
        }
    }

    // ===== PARTÍCULA (PSO) =====
    static class Particula {
        double[] pos; // p, r, b
        double[] vel;
        double[] melhorPos;
        double melhorFitness;
    }

    // ===== FUNÇÃO OBJETIVO =====
    static double avaliar(int[] hubs, double[] params) {

        double cobertura = Arrays.stream(hubs).sum() * params[0];
        double espera = 100.0 / (params[1] + 1);
        double energia = params[0] * 0.5;
        double interferencia = params[0] * 0.3;
        double violacao = 0;

        if (Arrays.stream(hubs).sum() > MAX_HUBS) {
            violacao += 100;
        }

        double F = 1.0 * cobertura
                - 0.8 * espera
                - 0.5 * energia
                - 0.3 * interferencia
                - 2.0 * violacao;

        return F;
    }

    // ===== GA =====
    static Individuo gerarIndividuo() {
        Individuo ind = new Individuo();

        for (int i = 0; i < NUM_LOCAIS; i++) {
            ind.hubs[i] = rand.nextBoolean() ? 1 : 0;
        }

        return ind;
    }

    static Individuo selecao(List<Individuo> pop) {
        return pop.get(rand.nextInt(pop.size()));
    }

    static Individuo crossover(Individuo a, Individuo b) {
        Individuo filho = new Individuo();

        for (int i = 0; i < NUM_LOCAIS; i++) {
            filho.hubs[i] = rand.nextBoolean() ? a.hubs[i] : b.hubs[i];
        }

        return filho;
    }

    static void mutacao(Individuo ind) {
        int i = rand.nextInt(NUM_LOCAIS);
        ind.hubs[i] = 1 - ind.hubs[i];
    }

    // ===== PSO =====
    static double[] executarPSO(int[] hubs) {

        Particula[] swarm = new Particula[PARTICULAS];

        double[] gBest = null;
        double gBestFit = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < PARTICULAS; i++) {
            swarm[i] = new Particula();

            swarm[i].pos = new double[]{
                    10 + rand.nextDouble() * 30, // p
                    1 + rand.nextDouble() * 4,   // r
                    20 + rand.nextDouble() * 60  // b
            };

            swarm[i].vel = new double[3];
            swarm[i].melhorPos = swarm[i].pos.clone();

            double fit = avaliar(hubs, swarm[i].pos);
            swarm[i].melhorFitness = fit;

            if (fit > gBestFit) {
                gBestFit = fit;
                gBest = swarm[i].pos.clone();
            }
        }

        for (int t = 0; t < ITERACOES_PSO; t++) {

            for (Particula p : swarm) {

                for (int d = 0; d < 3; d++) {
                    p.vel[d] = 0.5 * p.vel[d]
                            + 1.5 * rand.nextDouble() * (p.melhorPos[d] - p.pos[d])
                            + 1.5 * rand.nextDouble() * (gBest[d] - p.pos[d]);

                    p.pos[d] += p.vel[d];
                }

                double fit = avaliar(hubs, p.pos);

                if (fit > p.melhorFitness) {
                    p.melhorFitness = fit;
                    p.melhorPos = p.pos.clone();
                }

                if (fit > gBestFit) {
                    gBestFit = fit;
                    gBest = p.pos.clone();
                }
            }
        }

        return gBest;
    }

    // ===== EXECUÇÃO PRINCIPAL =====
    public static void main(String[] args) {

        for (int exec = 1; exec <= EXECUCOES; exec++) {

            System.out.println("\n===== EXECUÇÃO " + exec + " =====");

            List<Individuo> pop = new ArrayList<>();

            for (int i = 0; i < POP_SIZE; i++) {
                pop.add(gerarIndividuo());
            }

            Individuo melhorGlobal = null;
            double melhorFit = Double.NEGATIVE_INFINITY;

            for (int g = 0; g < GERACOES; g++) {

                System.out.println("\n--- Geração " + g + " ---");

                for (Individuo ind : pop) {

                    double[] params = executarPSO(ind.hubs);
                    ind.fitness = avaliar(ind.hubs, params);

                    System.out.println("Indivíduo: " + Arrays.toString(ind.hubs)
                            + " | Fitness: " + ind.fitness);

                    if (ind.fitness > melhorFit) {
                        melhorFit = ind.fitness;
                        melhorGlobal = ind;
                    }
                }

                List<Individuo> novaPop = new ArrayList<>();

                while (novaPop.size() < POP_SIZE) {
                    Individuo p1 = selecao(pop);
                    Individuo p2 = selecao(pop);

                    Individuo filho = crossover(p1, p2);

                    if (rand.nextDouble() < 0.1) {
                        mutacao(filho);
                    }

                    novaPop.add(filho);
                }

                pop = novaPop;
            }

            System.out.println("\n>>> MELHOR SOLUÇÃO <<<");
            System.out.println("Hubs ativos: " + Arrays.toString(melhorGlobal.hubs));
            System.out.println("Fitness: " + melhorFit);
        }
    }
}
