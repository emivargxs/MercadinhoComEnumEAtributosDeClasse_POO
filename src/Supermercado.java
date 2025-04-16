public class Supermercado {
	private final int nroCaixas;
	private double totalFaturamento;
	private double totalPerdas;
	private int totalClientesAtendidos;
	private int totalClientesNaoAtendidos;
	private double valorMenorCompra;
	private double valorMaiorCompra;
	private final Caixa[] caixas;
	private int timestamp = 0;

	public Supermercado(int nroCaixas) {
		this.nroCaixas = (nroCaixas > 0)? nroCaixas : 1;
		this.totalFaturamento = 0.0;
		this.totalPerdas = 0.0;
		this.totalClientesAtendidos = 0;
		this.totalClientesNaoAtendidos = 0;
		this.valorMenorCompra = Double.MAX_VALUE;
		this.valorMaiorCompra = Double.MIN_VALUE;

		// Aloca a colecao de caixas
		this.caixas = new Caixa[this.nroCaixas];

		// Cria cada um dos caixas
		for (int i = 0; i < this.nroCaixas; i++) {
			caixas[i] = new Caixa(ParametrosSimulacao.TAM_PADRAO_FILA);

			// Desabilitar o caixa com probabilidade conforme parametros
			if (Math.random() <= ParametrosSimulacao.PROBABILIDADE_CAIXA_INATIVO) {
				caixas[i].desativar();
			}

		}
		
	}

	public int getNroTotalCaixas() {
		return this.nroCaixas;
	}

	public int getNroCaixasPorStatus(Caixa.StatusCaixa status) {
		int qtd = 0;

		for (Caixa caixa : caixas)
			if (caixa.getStatus() == status)
				qtd++;

		return qtd;
	}

	/**
	 * Simula a chegada de um novo cliente.
	 * O cliente deverá ser automaticamente direcionado para o caixa ativo com a menor fila.
	 * Se não houver caixa disponível para atender o cliente (i.e. não há nenhum lugar em nenhuma fila),
	 * deve-se registrar uma perda de faturamento por cliente não atendido.
	 */
	public void simularNovoCliente() {

		// Chegou um novo cliente.
		// Sorteia um valor em compras, dentro dos limites configurados na simulação.
		double valorEmCompras =
				ParametrosSimulacao.VALOR_COMPRA_MIN
						+ Math.random() * (ParametrosSimulacao.VALOR_COMPRA_MAX - ParametrosSimulacao.VALOR_COMPRA_MIN);

		if (ParametrosSimulacao.DEBUG) {
			System.out.printf(">>> chegou novo cliente: R$ %.2f\n", valorEmCompras);
		}

		// Tenta direcionar o cliente para um dos caixas
		Caixa caixaDestino = localizarMelhorCaixa();

        if (caixaDestino != null) {
            // Há um caixa disponível para atender.
			// Coloca o cliente na fila do caixa encontrado e
			this.totalFaturamento += valorEmCompras;
			boolean ok = caixaDestino.adicionarClienteNaFila();
			if (!ok) {
				System.out.println("***BUG*** simularNovoCliente()");
			}

			// Atualiza indicadores (maior e menor valor de compra).
			if (valorEmCompras > this.valorMaiorCompra)
				this.valorMaiorCompra = valorEmCompras;

			if (valorEmCompras < this.valorMenorCompra)
				this.valorMenorCompra = valorEmCompras;

			if (ParametrosSimulacao.DEBUG) System.out.printf(">>> cliente direcionado para o caixa %d.\n", caixaDestino.getID());

        } else {
            // Não conseguimos atender. Registra a perda.
            this.totalPerdas += valorEmCompras;
			this.totalClientesNaoAtendidos++;
			if (ParametrosSimulacao.DEBUG) System.out.println(">>> filas cheias: registrando perda.");

		}
    }

	/**
	 * Encontra o caixa mais apropriado para um novo cliente.
	 * Procura o caixa ativo e livre com a menos quantidade de clientes na fila.
	 * Retorna null se nenhum caixa pode atende-lo.
	 */
	private Caixa localizarMelhorCaixa() {
		Caixa caixaDestino = null;
		int menorTamanhoDeFila = Integer.MAX_VALUE;

		for (Caixa caixa : caixas) {
			if (caixa.getStatus() == Caixa.StatusCaixa.Ativo) {    // É um caixa ativo?
				int clientesNessaFila = caixa.getNroClientesNaFila();
				int tamMaxFila = caixa.getTamanhoMaxFila();
				boolean cabeMaisUm = clientesNessaFila < tamMaxFila;

				if (cabeMaisUm) {
					menorTamanhoDeFila = clientesNessaFila;	// Encontrou uma fila menor
					caixaDestino = caixa;	// Lembra em qual caixa
				}
			}
		}

		return caixaDestino;
	}

	public double getTotalFaturamento() {
		return this.totalFaturamento;
	}

	public double getTotalPerdas() {
		return this.totalPerdas;
	}

	public int getNroClientesAtendidos() {
		return this.totalClientesAtendidos;
	}

	public int getNroClientesNaoAtendidos() {
		return this.totalClientesNaoAtendidos;
	}

	public double getValorMenorCompra() {
		return this.valorMenorCompra;
	}

	public double getValorMaiorCompra() {
		return this.valorMaiorCompra;
	}

	public void avancarTempo() {
		this.timestamp++;
		for (Caixa caixa : caixas) {
			boolean atendimentoFinalizado = caixa.atenderProximoCliente();

			if (atendimentoFinalizado) {
				this.totalClientesAtendidos++;
			}
		}
	}

	public void printStatus() {
		System.out.println("Timestamp " + this.timestamp);
		System.out.printf("  Clientes atendidos:     %3d\n", this.getNroClientesAtendidos());
		System.out.printf("  Clientes nao atendidos: %3d\n", this.getNroClientesNaoAtendidos());
		System.out.printf("  Faturamento:        R$ %6.2f\n", this.getTotalFaturamento());
		System.out.printf("  Perdas:             R$ %6.2f\n", this.getTotalPerdas());
		System.out.printf("  Valor menor compra: R$ %6.2f\n", this.getValorMenorCompra());
		System.out.printf("  Valor maior compra: R$ %6.2f\n", this.getValorMaiorCompra());
		System.out.println("\nCaixas");
		for (Caixa caixa : caixas) {
			System.out.printf("  [%d] (%s) %d/%d\n",
					caixa.getID(),
					caixa.getStatus(),
					caixa.getNroClientesNaFila(),
					caixa.getTamanhoMaxFila()
			);
		}
		System.out.println();
	}

}
