package eDoe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import util.ComparadorDeArrayDeInformacoesDeItem;
import util.ComparadorPorDescricao;
import util.TuplaDePontosDeMatchComItemEUsuario;
import util.Validacao;

import java.util.Scanner;
import java.util.TreeMap;
/**
 * Controlador do sistema, possui todas as informações e todos os métodos para o sistema funcionar.
 * @author Gustavo Gurjão Camargo Campos, Daniel de Matos Figueredo, Joao Victor Teodulo Wanderley.
 *
 */
public class Controller {	
	/**
	 * Mapa de usuários do sistema, possui todos os usuários cadastrados no sistema, associados
	 * ao seu identificador único: seu id(String).
	 */
	private Map<String,Usuario> usuarios;
	/**
	 * Mapa de todos os descritores do sistema, possui todos os descritores cadastrados,
	 * associados ao seu identificador único: sua descrição(String)
	 */
	private Map<String,Descritor> descritores;
	/**
	 * Contador para a adição de itens.
	 */
	private Map<Doacao,String> doacoes;
	private int idItens;
	/**
	 * Realizador da validação dos itens
	 */
	private Validacao validador;
	/**
	 * Construtor do controlador. Constrói o controlador a partir de um LinkedHashMap de usuários (mantendo a ordem de inserção),
	 * um TreeMap de descritores(Ordem alfabética das chaves), põe o idItens igual a zero para iniciar o sistema e constrói um novo
	 * validador.
	 */
	
	
	public Controller() {
		this.usuarios = new LinkedHashMap<String,Usuario>();
		this.descritores = new TreeMap<String,Descritor>();
		this.doacoes = new TreeMap<Doacao,String>();
		this.idItens = 0;
		this.validador = new Validacao();
	}
	/**
	 * Adiciona um doador ao sistema, a partir de seu id, nome,email, celular e classe.
	 * @param id identificador único do usuario
	 * @param nome nome do usuario
	 * @param email email do usuario
	 * @param celular celular do usuario
	 * @param classe classe à qual o usuario pertence, como igreja, pessoa_fisica, ONG, entre outras.
	 * @return o identificador único do doador
	 */
	public String adicionaDoador(String id, String nome, String email, String celular, String classe) {
		
		this.validador.validaAdicionaDoador(id, nome, email, celular, classe, this.usuarios);
		
		this.usuarios.put(id,new Usuario(id,nome,email,celular,classe,"doador"));
		return id;
		
	}
	/**
	 * Pesquisa um usuário de acordo com seu id
	 * @param id id do usuário a ser pesquisado
	 * @return a representação em String do usuário
	 */
	public String pesquisaUsuarioPorId(String id) {
		this.validador.validapesquisaUsuarioPorId(id, this.usuarios);

		return this.usuarios.get(id).toString();
	}
	/**
	 * Pesquisa usuários com um nome em específico
	 * @param nome nome a ser pesquisado
	 * @return retorna a representação em String de todos os usuários com esse nome em ordem de inserção
	 */
	public String pesquisaUsuarioPorNome(String nome) {
			
			this.validador.validapesquisaUsuarioPorNome(nome);
		
			String retorno = "";
			for (Usuario usuario : this.usuarios.values()) {
				if (usuario.getNome().equals(nome)) {retorno += usuario.toString() + " | ";}
			}
			
			if (retorno.trim().equals("")) {throw new IllegalArgumentException("Usuario nao encontrado: " + nome + ".");}
			
			else {
				return retorno.substring(0, retorno.length()-3);
			}	
	}
	/**
	 * Atualiza informações do usuário: nome, email ou celular
	 * @param id id do usuário a ser alterado
	 * @param nome novo nome do usuário
	 * @param email novo email do usuário
	 * @param celular novo celular do usuário
	 * @return nova representação em String do usuário
	 */
	public String atualizaUsuario(String id, String nome, String email, String celular) {
			this.validador.validaAtualizaUsuario(id,nome,email,celular,this.usuarios);
			
			if (nome != null && !nome.trim().equals("")) { 
				this.usuarios.get(id).setNome(nome);
			}
			
			if (celular != null && !celular.trim().equals("")) {
				this.usuarios.get(id).setCelular(celular);
			}
			
			if (email != null && !email.trim().equals("")) {
				this.usuarios.get(id).setEmail(email);
			}
			
			return this.usuarios.get(id).toString();
			
	}
	/**
	 * Remove um usuário e todos os itens pertencentes a ele do sistema de acordo com seu id
	 * @param id id do usuário a ser removido
	 */
	public void removeUsuario(String id) {
		this.validador.validaRemoveUsario(id,this.usuarios);
		
			for (Item item : this.usuarios.get(id).getItens().values()) {
				removeItem(item.getId(),id);
			}
			this.usuarios.remove(id);
		
	}
	/**
	 * Adiciona um novo descritor ao sistema
	 * @param descricao descrição do descritor do sistema
	 */
	public void adicionaDescritor(String descricao) {
		this.validador.validaAdicionaDescritor(descricao,this.descritores);

		this.descritores.put(descricao.toLowerCase(),new Descritor(descricao.toLowerCase()));

	}
	/**
	 * Adiciona um item para doação a um usuário
	 * @param idUsuario id do usuário que estara vinculado ao item
	 * @param descricaoItem descrição do item a ser vinculado
	 * @param quantidade quantidade do item a ser vinculado
	 * @param tags tags do item a ser vinculado
	 * @return o id do item a ser vinculado
	 */
	public int adicionaItem(String idUsuario, String descricaoItem, int quantidade, String tags) {
		this.validador.validaAdicionaItem(idUsuario,descricaoItem,quantidade,tags,this.usuarios);
		
		for (Item item : this.usuarios.get(idUsuario).getItens().values()) {
			if (item.getDescricao().toLowerCase().equals(descricaoItem.toLowerCase()) && item.getTags().equals(tags)) {
				
				if (!(this.usuarios.get(idUsuario).getStatus().equals("receptor"))) {
					int diferenca = quantidade - item.getQuant();
					
					this.descritores.get(descricaoItem).aumentaQuant(diferenca);
				}
				item.setQuant(quantidade);
					
				return item.getId();
			}
		}
		if (!(this.usuarios.get(idUsuario).getStatus().equals("receptor"))) {
		
			if (this.descritores.containsKey(descricaoItem.toLowerCase())) {
			this.descritores.get(descricaoItem.toLowerCase()).aumentaQuant(quantidade);
			}
			
			if (!this.descritores.containsKey(descricaoItem.toLowerCase())) {
			this.descritores.put(descricaoItem.toLowerCase(),new Descritor(descricaoItem.toLowerCase(),quantidade));
			}
		}
			
		this.usuarios.get(idUsuario).getItens().put(this.idItens,new Item(descricaoItem.toLowerCase(), quantidade, tags,idItens));
			
		this.idItens += 1;
			
		return (this.idItens-1);

	}
	/**
	 * Exibe a representação em String de um item do sistema
	 * @param id id do item a ser exibido
	 * @param idDoador id do doador ao qual pertence o item
	 * @return a representação em String do item
	 */
	public String exibeItem(int id, String idDoador) {
		this.validador.validaExibeItem(id,idDoador,this.usuarios);
	
		return this.usuarios.get(idDoador).getItens().get(id).toString();
	}
	/**
	 * Atualiza a quantidade ou as tags de um item a ser doado.
	 * @param id id do item a ser modificado
	 * @param idUsuario id do usuario ao qual o item esta atribuido
	 * @param quantidade nova quantidade do item
	 * @param tags novas tags do item
	 * @return a nova representação em String do item
	 */
	public String atualizaItem(int id, String idUsuario, int quantidade, String tags) {
		this.validador.validaAtualizaItem(id,idUsuario,quantidade,tags,this.usuarios);
		
			if (quantidade > 0) {
				if (!(this.usuarios.get(idUsuario).getStatus().equals("receptor"))) {
					if (this.usuarios.get(idUsuario).getItens().get(id).getQuant() > quantidade) {
						
						int diferenca = this.usuarios.get(idUsuario).getItens().get(id).getQuant() - quantidade;
						
						this.descritores.get(this.usuarios.get(idUsuario).getItens().get(id).getDescricao()).diminuiQuant(diferenca);
					}
					
					if (this.usuarios.get(idUsuario).getItens().get(id).getQuant() < quantidade) {
						
						int diferenca = quantidade - this.usuarios.get(idUsuario).getItens().get(id).getQuant();
						
						this.descritores.get(this.usuarios.get(idUsuario).getItens().get(id).getDescricao()).aumentaQuant(diferenca);
					}
				}
					
				this.usuarios.get(idUsuario).getItens().get(id).setQuant(quantidade);
				
			}
			
		if (tags != null && !tags.trim().equals("")) {
			
			this.usuarios.get(idUsuario).getItens().get(id).setTags(tags);
			
		}
		
		return this.usuarios.get(idUsuario).getItens().get(id).toString();
			
	}
	/**
	 * Remove um item para doação
	 * @param id id do item a ser removido
	 * @param idUsuario id do usuario ao qual o item esta atribuido
	 */
	public void removeItem(int id, String idUsuario) {
		this.validador.validaRemoveItem(id,idUsuario,this.usuarios);
		
		int diferenca = this.usuarios.get(idUsuario).getItens().get(id).getQuant();
		
		if (!(this.usuarios.get(idUsuario).getStatus().equals("receptor"))) {
			this.descritores.get(this.usuarios.get(idUsuario).getItens().get(id).getDescricao()).diminuiQuant(diferenca);
		}
		this.usuarios.get(idUsuario).getItens().remove(id); 

	}
	/**
	 * Le os receptores a partir de um arquivo csv
	 * @param caminho arquivo csv a ser lido
	 */
	public void lerReceptores(String caminho){
		Scanner sc = null;
		
		try {
			sc = new Scanner(new File(caminho));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String linha = null;
		
		while(sc.hasNextLine()) {
			
			linha = sc.nextLine();
			
			if (linha.equals("id,nome,E-mail,celular,classe"))
				continue;
			
			String[] dadosReceptor = linha.split(",");
			
			if (this.usuarios.containsKey(dadosReceptor[0])) {
				this.usuarios.get(dadosReceptor[0]).setNome(dadosReceptor[1]);
				this.usuarios.get(dadosReceptor[0]).setEmail(dadosReceptor[2]);
				this.usuarios.get(dadosReceptor[0]).setCelular(dadosReceptor[3]);
			}
			
			else {this.usuarios.put(dadosReceptor[0], new Usuario(dadosReceptor[0],dadosReceptor[1],dadosReceptor[2],dadosReceptor[3],dadosReceptor[4],"receptor"));}
		}
		
		sc.close();
		
	}
	/**
	 * Percorre o map "descritores" e retorna o toString() de todos eles separados por " | "
	 * @return o toString() de todos os descritores separados por " | "
	 */
	public String listaDescritorDeItensParaDoacao() {
		String s="";
		
		for (Descritor i : descritores.values()) {
			s+=i.toString()+" | ";
		}
		
		s=s.trim();
		
		if (!s.equals("")) {
			s=s.substring(0, s.length()-1);
		}
		
		return s.trim();
		
	}
	/**
	 * Percorre os itens de todos os doadores guardando eles em um TreeMap,deixando todos ordenados automaticamente, e retorna sua representacao em ordem de quantidade, maior para menor
	 * @return representacao,em string, de todos os itens de todos os doadores, em ordem de quantidade decrescente, no formato : '"idDoItem" - "descritor", tags: ["tags"], quantidade: "quantidade", doador: "nomeDoDoador"/"idDoUsuario"\';
	 * e separados por " | " 
	 */
	public String listaItensParaDoacao() {
		TreeMap<Item,Usuario> lista = new TreeMap<>();
		String s="";
		
		for (Usuario i : usuarios.values()) {
			if (i.getStatus().equals("doador"))
			for (Item j : i.getItens().values()) {
				lista.put(j, i);
			}
		}
		
		for (Entry<Item,Usuario> entry : lista.entrySet()) {
			s+=entry.getKey().toString() + ", doador: " + entry.getValue().getNome() + "/" + entry.getValue().getId() + " | ";
		}
		
		s=s.trim();
		
		if (!s.equals("")) {
			s=s.substring(0, s.length()-1);
		}
		
		return s.trim();
		
	}

	/**
	 * Pesquisa um item pela sua descricao e guarda em um TreeMap que automaticamente deixa ordenado em ordem alfabetica
	 * @param pesquisa termo que vai ser pesquisado na descricao dos itens
	 * @return todos os itens cuja descricao contem o termo pesquisado no formato :"idDoItem" - "descritor", tags: ["tags"], quantidade: "quantidade" | 
	 */
	public String pesquisaItemParaDoacaoPorDescricao(String pesquisa) {
		this.validador.validaPesquisaItemParaDoacaoPorDescricao(pesquisa);
		
		ArrayList<Item> itensValidos=new ArrayList<>();
		
		ComparadorPorDescricao comparador=new ComparadorPorDescricao();
		
		String s="";
		
		for (Usuario i : usuarios.values()) {
			for (Item j : i.getItens().values())
				if (j.getDescricao().toLowerCase().contains(pesquisa.toLowerCase())) {
					itensValidos.add(j);
			}
		}
		
		Collections.sort(itensValidos, comparador);
		
		for (Item i : itensValidos) {
			s+=i.toString()+" | ";
		}
		
		s=s.trim();
		
		if (!s.equals("")) {
			s=s.substring(0, s.length()-1);
		}
		
		return s.trim();
	}
	
	/**
	 * Percorre os itens de todos os doadores e retorna sua representacao em ordem de id do item, de menor id para maior id.
	 *
	 * @return representacao, em string, de todos os itens de todos os receptores, em ordem de id crescente, no formato : "idDoItem" - "descritor", tags: ["tags"], quantidade: "quantidade", receptor: "nomeDoReceptor"/"idDoUsuario"\ | 
	 */
	public String listaItensNecessarios() {
		
		ArrayList<String[]> listaParaImpressao = new ArrayList<>();
		String s="";
		
		for (Usuario i : usuarios.values()) {
			if (!(i.getStatus().equals("receptor"))) {
				continue;
			} else {
				for (Item o : i.getItens().values()) {
					String[] ArrayDeInformacoes = new String[2];
					ArrayDeInformacoes[0] = "" + o.getId();
					ArrayDeInformacoes[1] = " - " + o.getDescricaoETagsEQuantidades() + ", Receptor: " + i.getNome() + "/" + i.getId() + " | ";
					listaParaImpressao.add(ArrayDeInformacoes);
				}
			}
		}
		
		Collections.sort(listaParaImpressao, new ComparadorDeArrayDeInformacoesDeItem());
		
		for (String[] i: listaParaImpressao) {
			s += i[0] + i[1];
		}
		
		s = s.substring(0, s.length() - 3);
		s.trim();
		
		return s;
	}
	/**
	 * Percorre todos os itens doados e retorna uma representacao em String dos itens que possuem a mesma descricao do item necessario
	 *  ordenados pelos pontos de match(os que possuem o melhor match vem primeiro).
	 * @param idReceptor id do usuario receptor que guarda o item necessario
	 * @param idItemNecessario id do item necessario procurado
	 * @return uma representacao ordenada dos itens doados, que possuem a mesma descricao do item necessario, de maior ponto de match para menor no formato: '"idDoItem" - "descricaoDoItem", tags: ["tags"], quantidade: "quantidade", doador: "nomeDoDoador"/"idDoUsuario"'
	 * e separados por " | "
	 */
	public String match(String idReceptor, int idItemNecessario) {
		this.validador.validaMatch(idReceptor,idItemNecessario,this.usuarios);
		
		Item itemNecessario = this.usuarios.get(idReceptor).getItens().get(idItemNecessario);
		
		ArrayList<TuplaDePontosDeMatchComItemEUsuario> canditatosADoacao = new ArrayList<>();
		
		for (Usuario u : this.usuarios.values()) {
			if (u.getStatus().equals("receptor")) {
				continue;
			}
			for (Item i : u.getItens().values()) {
				if (!(i.getDescricao().equals(itemNecessario.getDescricao()))) {
					continue;
				}
				TuplaDePontosDeMatchComItemEUsuario pontosECandidatoADoacao = new TuplaDePontosDeMatchComItemEUsuario(calcularPontosDeMatch(itemNecessario,i),i,u);
				canditatosADoacao.add(pontosECandidatoADoacao);
			}
		}
		
		Collections.sort(canditatosADoacao);
		
		String retorno = "";
		for (TuplaDePontosDeMatchComItemEUsuario tupla : canditatosADoacao) {
			retorno += tupla.getItem().getId() + " - " + tupla.getItem().getDescricaoETagsEQuantidades() + ", doador: " + tupla.getUsuario().getNome() + "/" + tupla.getUsuario().getId() + " | ";
		}
		
		if (!(retorno.equals(""))){
			retorno = retorno.substring(0, retorno.length() - 3);
		}
		
		return retorno;
		
		
	}
	/**
	 * Calcula os pontos de match entre dois itens e retorna o valor calculado.
	 * @param itemNecessario item necessario requisitado
	 * @param candidatoADoacao item que esta sendo avaliado com relacao ao itemNecessario
	 * @return um valor equivalente aos pontos de match entre itemNecessario e candidatoADoacao
	 */
	private int calcularPontosDeMatch(Item itemNecessario,Item candidatoADoacao) {
		int pontos = 0;
		
		if (itemNecessario.getDescricao().equalsIgnoreCase(candidatoADoacao.getDescricao())) {
			pontos += 20;
		}
		
		String[] TagsDeItemNecessario = itemNecessario.getTags().split(",");
		String[] TagsDeCandidatoADoacao = candidatoADoacao.getTags().split(",");
		
		for (int i = 0; i < TagsDeItemNecessario.length; i++) {
			if (Arrays.asList(TagsDeCandidatoADoacao).contains(TagsDeItemNecessario[i])) {
				pontos += 5;
			}
		}
		
		if (TagsDeItemNecessario.length <= TagsDeCandidatoADoacao.length) {
			for (int i = 0; i < TagsDeItemNecessario.length; i++) {
				if (TagsDeItemNecessario[i].equalsIgnoreCase(TagsDeCandidatoADoacao[i])) {
					pontos += 5;
				}
			}
		}
			
		else {
			for (int i = 0; i < TagsDeCandidatoADoacao.length; i++) {
				if (TagsDeItemNecessario[i].equalsIgnoreCase(TagsDeCandidatoADoacao[i])) {
					pontos += 5;
				}
		}
	}
			
		
		return pontos;
	}
	/**
	 * Efetua uma doacao reduzindo a quantidade do item necessario e do item doado, além de registrar
	 *  a doacao no atributo doacoes do controlador
	 * @param idNecessario id do item requisitado
	 * @param idDoado id do item que sera doado
	 * @param data data em que a doacao foi realizada
	 * @return uma representacao em String da doacao no formato '"data" - doador: "nomeDoDoador"/"idDoDoador", item: "descricaoDoItem", quantidade: "quantidadeDoada", receptor: "nomeDoReceptor"/"idDoReceptor"'
	 */
	public String realizaDoacao(int idNecessario, int idDoado, String data) {
		this.validador.validaRealizaDoacao(idNecessario,idDoado,data);		
			
		String idDoador = "";
		String idReceptor = "";
		for (Usuario i : usuarios.values()) {
			for (Item j : i.getItens().values()) {
				if (j.getId() == idDoado) {
					idDoador = i.getId();
				}
				
				else if (j.getId() == idNecessario) {
					idReceptor = i.getId();
				}
			}
		}
		
		if (idReceptor.equals("")) throw new IllegalArgumentException("Item nao encontrado: " + idNecessario + ".");
		
		if (idDoador.equals("")) throw new IllegalArgumentException("Item nao encontrado: " + idDoado + ".");
		
		if (!usuarios.get(idDoador).getItens().get(idDoado).getDescricao().equals(usuarios.get(idReceptor).getItens().get(idNecessario).getDescricao())) {
			throw new IllegalArgumentException("Os itens nao tem descricoes iguais.");
		}
		
		String doador = usuarios.get(idDoador).getNome()+"/"+usuarios.get(idDoador).getId();
		String receptor = usuarios.get(idReceptor).getNome()+"/"+usuarios.get(idReceptor).getId();
		String descricao = usuarios.get(idDoador).getItens().get(idDoado).getDescricao();
		int qtd = Math.min(usuarios.get(idDoador).getItens().get(idDoado).getQuant(), usuarios.get(idReceptor).getItens().get(idNecessario).getQuant());
		
		doacoes.put(new Doacao(data, usuarios.get(idDoador).getItens().get(idDoado).getDescricao()), data + " - doador: " + doador + ", item: " + descricao + ", quantidade: " + qtd + ", receptor: " + receptor);
		
		int newQtdDoado = usuarios.get(idDoador).getItens().get(idDoado).getQuant() - qtd;
		int newQtdNecessario = usuarios.get(idReceptor).getItens().get(idNecessario).getQuant() - qtd;
		
		if (newQtdDoado > 0) atualizaItem(idDoado, idDoador, newQtdDoado, "");
		else removeItem(idDoado, idDoador);
		
		if (newQtdNecessario > 0) atualizaItem(idNecessario, idReceptor, newQtdNecessario, "");
		else removeItem(idNecessario, idReceptor);
		
		return  data + " - doador: " + doador + ", item: " + descricao + ", quantidade: " + qtd + ", receptor: " + receptor;
	
	}
	/**
	 * lista todas as doacoes realizadas
	 * @return retorna as representacoes de doacoes na ordem de data e caso tenham a mesma data, a descricao.
	 */
	public String listaDoacoes() {
		ArrayList<String> lista =new ArrayList<>();
		
		for (String doacao : this.doacoes.values()) {
			lista.add(doacao);
		}
		
		return String.join(" | ", lista);
	}
	/**
	 * Finaliza o sistema, guardando as modificacoes em usuarios, doacoes, descritores, e o idItens.Depois ele zera todas as informacoes do sistema.
	 * @throws IOException
	 */
	public void finalizaSistema() throws IOException {
		 FileOutputStream arqUser = new FileOutputStream("arquivos_sistema/usuarios.txt");
	        
		 ObjectOutputStream user = new ObjectOutputStream(arqUser);
			
		 ArrayList<Usuario> users = new ArrayList<Usuario>();
			
		 for (Usuario u : this.usuarios.values()) {
			 users.add(u);
			}
	    
		 user.writeObject(users);
		 
		 user.close();
		 
		 FileOutputStream arqDoac = new FileOutputStream("arquivos_sistema/Doacoes.txt");
	     
		 ObjectOutputStream doac = new ObjectOutputStream(arqDoac);
			
		 TreeMap<Doacao,String> doacao = new TreeMap<Doacao,String>();
		 
		 for (Entry<Doacao, String> entry : this.doacoes.entrySet()) {
			 doacao.put(entry.getKey(), entry.getValue());
			}
		 
		 doac.writeObject(doacao);
		 
		 doac.close();
		 
		 FileOutputStream arqDesc = new FileOutputStream("arquivos_sistema/Descritores.txt");
	     
		 ObjectOutputStream desc = new ObjectOutputStream(arqDesc);
			
		 TreeMap<String, Descritor> descritors = new TreeMap<String, Descritor>();
		 
		 for (Entry<String, Descritor> entry : this.descritores.entrySet()) {
			 descritors.put(entry.getKey(), entry.getValue());
			}
		 
		 desc.writeObject(descritors);
		 
		 desc.close();
		 
		 FileOutputStream arqItens = new FileOutputStream("arquivos_sistema/NdeItens.txt");
	     
		 ObjectOutputStream Item = new ObjectOutputStream(arqItens);
			
		 int itens = this.idItens;
		 
		 Item.writeObject(itens);
		 
		 Item.close();	
		 
		 // As seguintes linhas simulam o reinicio do sistema
		 this.usuarios = new LinkedHashMap<String,Usuario>();
		 this.descritores = new TreeMap<String,Descritor>();
		 this.doacoes = new TreeMap<Doacao,String>();
		 this.idItens = 0;
		 this.validador = new Validacao();
		
	}
	/**
	 * Inicia o sistema, lendo os arquivos salvos e recuperando o estado anterior do sistema.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void iniciaSistema() throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream("arquivos_sistema/usuarios.txt");
        
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        ArrayList<Usuario> woi = new ArrayList<>();
        
        woi = (ArrayList<Usuario>)ois.readObject();
        
        for (Usuario u : woi){
        	
         this.usuarios.put(u.getId(), u);

        }
        
        ois.close();
        
        FileInputStream fis2 = new FileInputStream("arquivos_sistema/Doacoes.txt");
        
        ObjectInputStream ois2 = new ObjectInputStream(fis2);
        
        TreeMap<Doacao,String> woi2 = new TreeMap<>();
        
        woi2 = (TreeMap<Doacao,String>)ois2.readObject();
        
        for (Entry<Doacao, String> entry : woi2.entrySet()) {
			
			this.doacoes.put(entry.getKey(), entry.getValue());
		
        }
        
        ois2.close();
        
        FileInputStream fis3 = new FileInputStream("arquivos_sistema/Descritores.txt");
        
        ObjectInputStream ois3 = new ObjectInputStream(fis3);
        
        TreeMap<String,Descritor> woi3 = new TreeMap<>();
        
        woi3 = (TreeMap<String,Descritor>)ois3.readObject();
        
        for (Entry<String, Descritor> entry : woi3.entrySet()) {
			
			this.descritores.put(entry.getKey(), entry.getValue());
		
        }
        
        ois3.close();
        
        FileInputStream fis4 = new FileInputStream("arquivos_sistema/NdeItens.txt");
        
        ObjectInputStream ois4 = new ObjectInputStream(fis4);
        
        this.idItens = (Integer)ois4.readObject();        
        
        ois4.close();
		
	}
	
	
}
