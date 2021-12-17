package com.hepta.treino.entities;

import java.io.Serializable;

public class Carro implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3907357407389333246L;
	
	
	private int id_carro;
	private String modelo;
	private String cor;
	private String dataentrada;
	private Montadora montadora;

	public int getId() {
		return id_carro;
	}

	public void setId(int id) {
		this.id_carro = id;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	public String getDataEntrada() {
		return dataentrada;
	}

	public void setDataEntrada(String dataEntrada) {
		this.dataentrada = dataEntrada;
	}

	public Montadora getMontadora() {
		return montadora;
	}

	public void setMontadora(Montadora montadora) {
		this.montadora = montadora;
	}

}
