package com.hepta.treino.rest;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;

import com.hepta.treino.entities.Carro;
import com.hepta.treino.entities.Foto;
import com.hepta.treino.entities.Montadora;
import com.hepta.treino.persistence.DAO;

@Path("/concessionaria")
public class Service 
{
	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	private DAO dao;

	public Service() {
		dao = new DAO();
	}	
	
	protected void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	@Path("/carro")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response createCarro(Carro carro)
	{
		try {
			dao.saveCarro(carro);
		} catch (Exception e) 
		{
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao cadastrar o Carro").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/montadora")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response createMontadora(Montadora mont)
	{
		try {
			dao.saveMontadora(mont);
		} catch (Exception e) 
		{
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao inserir a Montadora").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/carro/foto/{id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response uploadFoto(@PathParam("id") int id, Foto foto)
	{
		//O arquivo será escrito em um volume compartilhado com o Banco via Docker
		int nome = (int) Math.floor(Math.random()*1000);
		foto.setId_Carro(id);
		
		String filePath = "D:\\Files\\"+foto.getId_Carro()+"\\"+nome+".png";
		
		
		try {
			String base64 = foto.getFile();
			byte[] arquivo = Base64.getDecoder().decode(base64);
			
			FileUtils.writeByteArrayToFile(new File(filePath), arquivo);
		}catch(Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao salvar a foto em disco").build();
		}
		//Localização no Filesystem do banco
		String caminhoAdaptado = filePath.replace("D:\\Files\\", "/Files/");
		caminhoAdaptado = caminhoAdaptado.replace("\\", "/");
		foto.setPath(caminhoAdaptado);
		
		try
		{			
			dao.saveFoto(foto);
			
		}catch(SQLException e)
		{
			try {
				FileUtils.delete(new File(filePath));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao inserir a Foto no banco").build();
		}

		return Response.status(Status.OK).build();
	}
	
	@Path("/carro/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response findCarro(@PathParam("id") int id)
	{
		Carro carro = null;
		try {
			carro = dao.findCarro(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar o Carro").build();
		}

		return Response.status(Status.OK).entity(carro).build();
	}
	
	@Path("/montadora/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response findMontadora(@PathParam("id") int id)
	{
		Montadora mont = null;
		try {
			mont = dao.findMontadora(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar a Montadora").build();
		}

		return Response.status(Status.OK).entity(mont).build();
	}
	
	@Path("/carro")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getCarros()
	{
		List<Carro> carros = new ArrayList<>();
		try {
			carros = dao.getCarros();
			carros.sort((x,y) -> x.getMontadora().getNome().compareTo(y.getMontadora().getNome()));
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar os Carros").build();
		}

		GenericEntity<List<Carro>> entity = new GenericEntity<List<Carro>>(carros) {
		};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@Path("/montadora")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getMontadoras()
	{
		List<Montadora> monts = new ArrayList<>();
		try {
			monts = dao.getMontadoras();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar as Montadoras").build();
		}

		GenericEntity<List<Montadora>> entity = new GenericEntity<List<Montadora>>(monts) {
		};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@Path("/carro/foto/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getFotosCarro(@PathParam("id") int id)
	{
		List<Foto> fotos = new ArrayList<>();
		try {
			fotos = dao.getFotos(id);
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar as Fotos do Carro").build();
		}
		
		try {
			fotos.forEach(foto -> {
					String caminho = "D:" + foto.getPath();
					caminho = caminho.replace("/", "\\");
					File file = new File(caminho);
					try {
						byte[] arquivo = FileUtils.readFileToByteArray(file);
						String base64 = Base64.getEncoder().encodeToString(arquivo);
						foto.setFile(base64);
					} catch (IOException e) {
						e.printStackTrace();
					}
					});
		}catch(Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao carregar as Fotos").build();
		}

		GenericEntity<List<Foto>> entity = new GenericEntity<List<Foto>>(fotos) {
		};
		return Response.status(Status.OK).entity(entity).build();
	}
	
	@Path("/carro/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PUT
	public Response updateCarro(@PathParam("id") int id, Carro carro)
	{
		carro.setId(id);
		try {
			dao.editCarro(carro);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao editar as informações do Carro").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/montadora/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@PUT
	public Response updateMontadora(@PathParam("id") int id, Montadora mont)
	{
		try {
			dao.editMontadora(mont);
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao editar as informações da Montadora").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/carro/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public Response deleteCarro(@PathParam("id") int id)
	{
		try {
			dao.deleteCarro(id);
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar o Carro").build();
		}
		try {
			
			String caminho = "D:\\Files\\" + id;
			
			File pasta = new File(caminho);
			if(pasta.exists())
			{
				File[] arquivos = pasta.listFiles();
				
				for (File arquivo : arquivos) {
					arquivo.delete();
				}
				pasta.delete();
			}
		}catch(Exception e)
		{
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar as Fotos").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/montadora/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public Response deleteMontadora(@PathParam("id") int id)
	{
		
		try {
			dao.deleteMontadora(id);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar a Montadora").build();
		}
		try {
			List<Carro> lista = new ArrayList<>();
			lista = dao.getCarrosPorMontadora(id);
			
			lista.forEach(carro -> {
				String caminho = "D:\\Files\\" + carro.getId()+"\\";
				
				File pasta = new File(caminho);
				if(pasta.exists())
				{
					String[] arquivos = pasta.list();
					
					for (String arquivo : arquivos) {
						File currentFile = new File(pasta.getPath(),arquivo);
					    currentFile.delete();
					}
					pasta.delete();
				}
			});
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar as Fotos").build();
		}
		return Response.status(Status.OK).build();
	}
	
	@Path("/carro/foto/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public Response deleteFoto(@PathParam("id") int id, Foto foto)
	{
		try {
			dao.deleteFoto(id);
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Erro ao deletar a Foto do Banco").build();
		}
		
		try {
			String caminho = "D:" + foto.getPath();
			caminho = caminho.replace("/", "\\");
			File file = new File(caminho);
			
			file.delete();
		}catch(Exception e)
		{
			return Response.status(Status.OK).entity("Erro ao deletar a Foto do Filesystem").build();
		}
		
		return Response.status(Status.OK).build();
	}
	
	

	
	

}
