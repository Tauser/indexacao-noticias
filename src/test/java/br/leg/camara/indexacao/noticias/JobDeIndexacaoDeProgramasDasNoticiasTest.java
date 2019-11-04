package br.leg.camara.indexacao.noticias;

import br.leg.camara.indexacao.api.Configuracoes;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static br.leg.camara.indexacao.noticias.JobSqlParametrizavelNoticia.PARAMETRO_ID_NOTICIA;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JobDeIndexacaoDeProgramasDasNoticiasTest extends TesteDeIntegracaoComBancoH2 {

	private static final String PREFIXO_ARQUIVO_H2 = "programas";

	@BeforeClass
	public static void criarBanco() throws Exception {
		criarBancoDeDados(PREFIXO_ARQUIVO_H2);
	}

	@AfterClass
	public static void apagarBanco() throws IOException {
		removerArquivosTemporariosDoH2(PREFIXO_ARQUIVO_H2);
	}

	@Test
	public void configurarNaoDeveLancarExcecao() {
		criarJobConfigurado();
	}

	private JobDeIndexacaoDeProgramasDasNoticias criarJobConfigurado() {
		Configuracoes configuracoes = criarConfiguracoesBancoMemoria(PREFIXO_ARQUIVO_H2);
		JobDeIndexacaoDeProgramasDasNoticias job = new JobDeIndexacaoDeProgramasDasNoticias();
		job.setNomesDeCamposEmCaixaAlta(true);
		job.configurar(configuracoes);
		return job;
	}

	@Test
	public void quantidadeDeDocumentosDeveRetornarNumeroDeNoticiasComPrograma() {
		JobDeIndexacaoDeProgramasDasNoticias job = criarJobConfigurado();
		assertEquals(3, job.quantidadeDeDocumentos());
	}

	@Test
	public void proximoDocumentoPrimeiraChamada() {
		JobDeIndexacaoDeProgramasDasNoticias job = criarJobConfigurado();

		job.iniciar(ParametrosExecucao.vazio());
		Map<String, ?> primeiro = job.proximoDocumento();

		assertNotNull(primeiro);
		//como o H2 retorna o nome das colunas sempre em Maiúsculas, estamos usando os nomes em maiúsculas
		assertEquals(1, primeiro.get("ID"));
		assertEquals(Arrays.asList("Titulo programa radio 1", "Titulo programa radio 2"), primeiro.get("PROGRAMAS"));
	}

	@Test
	public void proximoDocumentoSegundaChamada() {
		JobDeIndexacaoDeProgramasDasNoticias job = criarJobConfigurado();

		job.iniciar(ParametrosExecucao.vazio());
		job.proximoDocumento();
		Map<String, ?> segundo = job.proximoDocumento();

		assertNotNull(segundo);
		//como o H2 retorna o nome das colunas sempre em Maiúsculas, estamos usando os nomes em maiúsculas
		assertEquals(2, segundo.get("ID"));
		assertEquals(singletonList("Titulo programa radio 2"), segundo.get("PROGRAMAS"));
	}

	@Test
	public void proximoDocumentoTerceiraChamada() {
		JobDeIndexacaoDeProgramasDasNoticias job = criarJobConfigurado();

		job.iniciar(ParametrosExecucao.vazio());
		job.proximoDocumento();
		job.proximoDocumento();
		Map<String, ?> terceiro = job.proximoDocumento();

		assertNotNull(terceiro);
		//como o H2 retorna o nome das colunas sempre em Maiúsculas, estamos usando os nomes em maiúsculas
		assertEquals(3, terceiro.get("ID"));
		assertEquals(singletonList("Titulo programa radio 3"), terceiro.get("PROGRAMAS"));
	}

	@Test
	public void execucaoParametrizadaDeveRetornarProgramasDeUmaNoticiaSomente() {
		JobDeIndexacaoDeProgramasDasNoticias job = criarJobConfigurado();
		ParametrosExecucao parametros = ParametrosExecucao.comValorSimples(PARAMETRO_ID_NOTICIA, "2");
		job.iniciar(parametros);

		assertEquals(1, job.quantidadeDeDocumentos());

		Map<String, ?> doc = job.proximoDocumento();
		assertEquals(2, doc.get("ID"));
		assertEquals(singletonList("Titulo programa radio 2"), doc.get("PROGRAMAS"));
	}
}