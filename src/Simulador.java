public class Simulador {
    public static void main(String[] args) {
        System.out.println("=========================");
        System.out.println("Simulador de Supermercado");
        System.out.println("=========================");

        Supermercado supermercado = new Supermercado(ParametrosSimulacao.NRO_CAIXAS);

        // Simular a chegada de clientes
        final int NRO_CLIENTES = ParametrosSimulacao.NRO_CLIENTES_SIMULAR;
        for (int i = 0; i < NRO_CLIENTES; i++) {
            supermercado.simularNovoCliente();
            supermercado.avancarTempo();
            supermercado.printStatus();
        }

        System.out.println("=========================");
        System.out.println("           FIM           ");
        System.out.println("=========================");
    }
}