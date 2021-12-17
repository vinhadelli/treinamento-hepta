package com.hepta.treino.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.hepta.treino.entities.Carro;
import com.hepta.treino.entities.Foto;
import com.hepta.treino.entities.Montadora;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class treinoTest
{
	private static Montadora montadora;
	private static Carro carro;
	private static Foto foto;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		montadora = new Montadora();
		montadora.setNome("Montadora Teste");
		
		carro = new Carro();
		carro.setModelo("Modelo Teste");
		carro.setCor("Cor Teste");
		carro.setDataEntrada("2021-12-17");
		
		foto = new Foto();
	}
	
	
	@Test
	@Order(1)
	void testMontadoraCreate()
	{
		Service service = new Service();
		Response response = null;
		try {		
			response = service.createMontadora(montadora);		
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.toString());
		}
		assertEquals(200, response.getStatus(),"Erro interno na criacao da Montadora");	
	}
	
	
	@Test
	@Order(2)
	@SuppressWarnings("unchecked")
	void testMontadoraGet()
	{
		Service service = new Service();
		Response response = null;
		List<Montadora> lista = new ArrayList<>();
		
		try {
			response = service.getMontadoras();
			lista = (List<Montadora>) response.getEntity();
			
			lista.forEach(mont -> {if(mont.getNome().equals(montadora.getNome()))
									{
										montadora.setId(mont.getId());
									}});		
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		assertEquals(200, response.getStatus(),"Erro interno no resgate das Montadoras");			
	}
	
	@Test
	@Order(3)
	void testCarroCreate()
	{
		Service service = new Service();
		Response response = null;
		carro.setMontadora(montadora);
		try {		
			response = service.createCarro(carro);		
		} catch (Exception e) {
			e.printStackTrace();
			fail (e.toString());
		}
		assertEquals(200, response.getStatus(),"Erro interno na criacao do Carro");	
	}
	
	@Test
	@Order(4)
	@SuppressWarnings("unchecked")
	void testCarroGet()
	{
		Service service = new Service();
		Response response = null;
		List<Carro> lista = new ArrayList<>();
		
		try {
			response = service.getCarros();
			lista = (List<Carro>) response.getEntity();
			
			lista.forEach(car -> {if(car.getModelo().equals(carro.getModelo())) 
									{
										carro.setId(car.getId());
									}});			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		if(response.getStatus() == 200)
			assertEquals(200, response.getStatus(),"Erro interno na requisição do Carro");			
	}
	
	@Test
	@Order(5)
	void testFotoUpload()
	{
		Service service = new Service();
		Response response = null;
		
		foto.setId_Carro(carro.getId());
		foto.setPath("D:\\Files\\Teste.png");
		try {
			File file = new File(foto.getPath());
			
			byte[] arquivo = FileUtils.readFileToByteArray(file);
			String base64 = Base64.getEncoder().encodeToString(arquivo);
			foto.setFile(base64);
			
			response = service.uploadFoto(foto.getId_Carro(), foto);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		File teste = new File("D:\\Files\\"+foto.getId_Carro());
		
		if(response.getStatus() == 200)
			assertTrue(teste.exists(), "Erro interno no Upload da Foto");	
	}
	
	@Test
	@Order(6)
	void testMontadoraEdit()
	{
		Service service = new Service();
		Response response = null;
		Montadora editada = null;
		
		montadora.setNome("Montadora Editada");
		
		try {
			response = service.updateMontadora(montadora.getId(), montadora);
			if (response.getStatus() == 200)
			{
				response = service.findMontadora(montadora.getId());
				editada = (Montadora) response.getEntity();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		if(response.getStatus() == 200)
			assertEquals(montadora.getNome(), editada.getNome(), "Erro interno na Edição da Montadora");
	}
	
	@Test
	@Order(7)
	void testCarroEdit()
	{
		Service service = new Service();
		Response response = null;
		Carro editado = null;
		
		carro.setModelo("Modelo Editado");
		
		try {
			response = service.updateCarro(carro.getId(), carro);
			if (response.getStatus() == 200)
			{
				response = service.findCarro(carro.getId());
				editado = (Carro) response.getEntity();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		if(response.getStatus() == 200)
			assertEquals(carro.getModelo(), editado.getModelo(), "Erro interno na Edição do Carro");
	}
	
	@Test
	@Order(8)
	@SuppressWarnings("unchecked")
	void testFotoDelete()
	{
		Service service = new Service();
		Response response = null;
		
		List<Foto> fotos = new ArrayList<>();
		
		try {
			response = service.getFotosCarro(foto.getId_Carro());
			if (response.getStatus() == 200)
			{
				fotos = (List<Foto>) response.getEntity();
				
				fotos.forEach(fot -> 
					{
						foto.setId(fot.getId());
						foto.setPath(fot.getPath());
						foto.setFile(fot.getFile());
					});
				
				response = service.deleteFoto(foto.getId(), foto);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		
		assertEquals(200, response.getStatus(),"Erro interno na exclusão da Foto");	
	}
	
	@Test
	@Order(9)
	void testCarroDelete()
	{
		Service service = new Service();
		Response response = null;
		
		try {
			response = service.deleteCarro(carro.getId());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		assertEquals(200, response.getStatus(),"Erro interno na exclusão do Carro");	
	}
	
	@Test
	@Order(10)
	void testMontadoraDelete()
	{
		Service service = new Service();
		Response response = null;
		
		try {
			response = service.deleteMontadora(montadora.getId());

		} catch (Exception e)
		{
			e.printStackTrace();
			fail (e.toString());
		}
		assertEquals(200, response.getStatus(),"Erro interno na exclusão do Carro");
	}
}