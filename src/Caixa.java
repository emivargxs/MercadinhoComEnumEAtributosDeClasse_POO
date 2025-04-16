public class Caixa {

	public enum StatusCaixa { Ativo, Inativo };

	private static int idGerador = 0;
	private final int id;
	private final int tamMaxFila;
	private int nroClientesNaFila;
	private StatusCaixa status;

	/**
	 * Cria um novo caixa, com o tamanho máximo de fila especificado. Atribui automaticamnete o seu id.
	 */
	public Caixa(int tamMaxFila) {
		idGerador++;			// Gera o novo id
		this.id = idGerador;	// Atribui o novo id para este caixa
		if (tamMaxFila > 0) {
		this.tamMaxFila = tamMaxFila;
		} else {
    			this.tamMaxFila = ParametrosSimulacao.TAM_PADRAO_FILA;
		}
			
		//this.tamMaxFila = (tamMaxFila > 0)? tamMaxFila : ParametrosSimulacao.TAM_PADRAO_FILA;
		this.nroClientesNaFila = 0;
		this.ativar();
	}

	/**
	 * Obtém o id do caixa, que foi gerado automaticamente na inicialização.
	 */
	public int getID() {
		return this.id;
	}

	public int getTamanhoMaxFila() {
		return this.tamMaxFila;
	}

	/**
	 * Retorna o número de clientes atualmente na fila.
	 */
	public int getNroClientesNaFila() {
		return this.nroClientesNaFila;
	}

	/**
	 * Ativa o caixa, permitindo receber novos clientes na fila.
	 */
	public boolean ativar() {
		this.status = StatusCaixa.Ativo;
		return true;
	}

	/**
	 * Desativa o caixa se a fila estiver vazia, retornando true. Se houver pelo menos um cliente na fila, não desativa e retorna false.
	 */
	public boolean desativar() {
		if (this.nroClientesNaFila > 0) {
			return false;	// Não desativar se houver alguém na fila.
		} else {
			this.status = StatusCaixa.Inativo;
			return true;
		}
	}

	/**
	 * Acrescenta um cliente na fila, se for possível.
	 * Retorna true se foi possível acrescentar.
	 */
	public boolean adicionarClienteNaFila() {
		if (status == StatusCaixa.Inativo)
			return false;	// Não pode adicionar se caixa estiver inativo

		if (this.nroClientesNaFila == getTamanhoMaxFila())
			return false;	// Não pode adicionar se a fila estiver cheia

		// Adiciona e indica sucesso
		this.nroClientesNaFila++;
		return true;
	}

	/**
	 * Atende o próximo cliente da fila, se existir.
	 * Retorna true se o cliente foi atendido, com base na probabilidade definida na simulação.
	 */
	public boolean atenderProximoCliente() {
		if (status == StatusCaixa.Inativo)
			return false;	// Não pode atender se caixa estiver inativo

		if (this.nroClientesNaFila == 0)
			return false;	// Se não houver clientes na fila, não houve atendimento.

		// Há pelo menos um cliente na fila.
		// Utiliza a definição de probabilidade configurada para determinar se atende ou não o próximo cliente.
		boolean atender = (Math.random() <= ParametrosSimulacao.PROBABILIDADE_ATENDIMENTO);

		if (!atender) {		// Não foi dessa vez...
			if (ParametrosSimulacao.DEBUG) System.out.println(">>> cliente ainda nao terminou no caixa " + this.id);
			return false;
		}

		// Atende o próximo cliente, removendo-o da fila.
		if (ParametrosSimulacao.DEBUG) System.out.println(">>> cliente finalizou compra no caixa " + this.id);
		this.nroClientesNaFila--;
		return true;
	}

	public StatusCaixa getStatus() {
		return this.status;
	}

}
