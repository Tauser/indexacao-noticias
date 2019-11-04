package br.leg.camara.indexacao.noticias;

import br.leg.camara.indexacao.api.JobAgregadorDeDocumentos;

/**
 * Job que agrega todas os programas de uma notícia, retornando isso em um único documento a ser indexado/atualizado
 */
public class JobDeIndexacaoDeProgramasDasNoticias extends JobAgregadorDeDocumentos<JobDeListagemDeProgramas> {

	@Override
	protected JobDeListagemDeProgramas criarJobAlvo() {
		return new JobDeListagemDeProgramas();
	}

	@Override
	protected String nomeDoCampoAgregado() {
		return JobDeListagemDeProgramas.NOME_CAMPO_PROGRAMA;
	}

	@Override
	protected String nomeDoCampoComResultadoDaAgregacao() {
		return "programas";
	}

	@Override
	public String nome() {
		return "programas-noticias";
	}

	@Override
	public String nomeDoIndice() {
		return JobDeIndexacaoDeNoticias.NOME_DO_INDICE;
	}

	@Override
	public long quantidadeDeDocumentos() {
		return getJobAlvo().quantidadeDeNoticiasComPrograma();
	}
}
