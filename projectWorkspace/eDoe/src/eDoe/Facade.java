package eDoe;

public class Facade {
	private Controller c;
	
	public Facade() {this.c = new Controller();}
	
	public String adicionaDoador(String id, String nome, String email,String celular, String classe) {
		return c.adicionaDoador(id,nome,email,celular,classe);
	}
	
	public String pesquisaUsuarioPorId(String id) {
		return c.pesquisaUsuarioPorId(id);
	}
	
	public String pesquisaUsuarioPorNome(String nome) {
		return c.pesquisaUsuarioPorNome(nome);
	}
	
	public String atualizaUsuario(String id, String nome, String email, String celular) {
		return c.atualizaUsuario(id,nome,email,celular);
	}
	
	public void removeUsuario(String id) {
		c.removeUsuario(id);
	}
	
	public void adicionaDescritor(String descricao) {
		c.adicionaDescritor(descricao);
	}
	
	public int adicionaItemParaDoacao(String idDoador, String descricaoItem, int quantidade, String tags) {
		return c.adicionaItemParaDoacao(idDoador,descricaoItem,quantidade,tags);
	}
	
	public String exibeItem(int id, String idDoador) {
		return c.exibeItem(id,idDoador);
	}
	
	public String atualizaItemParaDoacao(int id, String idDoador, int quantidade, String tags) {
		return c.atualizaItemParaDoacao(id,idDoador,quantidade,tags);
	}
	
	public void removeItemParaDoacao(int id, String idDoador) {
		c.removeItemParaDoacao(id,idDoador);
	}
	
}