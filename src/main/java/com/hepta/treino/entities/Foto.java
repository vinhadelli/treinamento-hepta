package com.hepta.treino.entities;

import java.io.Serializable;

public class Foto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3911160814564428962L;
	
	private int id_foto;
	private int id_carro;
	private String path;
	private String file;
	
	
	public int getId() {
		return id_foto;
	}
	public void setId(int iD_FOTO) {
		id_foto = iD_FOTO;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String pATH) {
		path = pATH;
	}
	public int getId_Carro() {
		return id_carro;
	}
	public void setId_Carro(int iD_CARRO) {
		id_carro = iD_CARRO;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	


}
