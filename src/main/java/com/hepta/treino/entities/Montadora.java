package com.hepta.treino.entities;

import java.io.Serializable;

public class Montadora implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3385313053282931859L;
	
	private int id_montadora;
	private String nome;

	public Montadora(int id, String nome) 
	{
		setId(id);
		setNome(nome);
	}

	public Montadora() {
		
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getId() {
		return id_montadora;
	}

	public void setId(int id) {
		this.id_montadora = id;
	}

}
