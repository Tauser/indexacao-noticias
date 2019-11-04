package br.leg.camara.indexacao.noticias;

import br.leg.camara.indexacao.api.Configuracoes;
import br.leg.camara.indexacao.api.ParametrosExecucao;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static br.leg.camara.indexacao.noticias.JobSqlParametrizavelNoticia.PARAMETRO_ID_NOTICIA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JobDeIndexacaoDeNoticiasTest extends TesteDeIntegracaoComBancoH2 {

	private static final String PREFIXO_ARQUIVO_H2 = "noticias";

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

	private JobDeIndexacaoDeNoticias criarJobConfigurado() {
		Configuracoes configuracoes = criarConfiguracoesBancoMemoria(PREFIXO_ARQUIVO_H2);
		JobDeIndexacaoDeNoticias job = new JobDeIndexacaoDeNoticias();
		job.configurar(configuracoes);
		return job;
	}

	@Test
	public void quantidadeDeDocumentosDeveRetornarNumeroCorretoDeRegistros() {
		JobDeIndexacaoDeNoticias job = criarJobConfigurado();
		assertEquals(4, job.quantidadeDeDocumentos());
	}

	@Test
	public void proximoDocumentoPrimeiraChamada() {
		JobDeIndexacaoDeNoticias job = criarJobConfigurado();

		job.iniciar(ParametrosExecucao.vazio());
		Map<String, ?> documento = job.proximoDocumento();

		assertNotNull(documento);
		//como o H2 retorna o nome das colunas sempre em Maiúsculas, estamos usando os nomes em maiúsculas
		assertEquals(1, documento.get("ID"));
		assertEquals("Titulo teste 1", documento.get("TITULO"));
		assertEquals("Conteudo teste agencia 1 Materia 1 Rodape", documento.get("MATERIA"));
	}

	@Test
	public void execucaoParametrizadaDeveRetornarSomenteUmaNoticia() {
		JobDeIndexacaoDeNoticias job = criarJobConfigurado();
		ParametrosExecucao parametros = ParametrosExecucao.comValorSimples(PARAMETRO_ID_NOTICIA, "1");
		job.iniciar(parametros);

		assertEquals(1, job.quantidadeDeDocumentos());

		Map<String, ?> doc = job.proximoDocumento();
		assertEquals(1, doc.get("ID"));
		assertEquals("Titulo teste 1", doc.get("TITULO"));
	}
}