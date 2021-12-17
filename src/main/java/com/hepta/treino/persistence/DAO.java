package com.hepta.treino.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.hepta.treino.entities.Carro;
import com.hepta.treino.entities.Foto;
import com.hepta.treino.entities.Montadora;


public class DAO
{
	private Connection con;
	
	public void saveMontadora(Montadora mont) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "INSERT INTO Montadora " +
				     "(NOME) "+
					 "VALUES (?);";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, mont.getNome());
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}
	}
	
	public void saveFoto(Foto foto) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "INSERT INTO Foto " +
				     "(FK_CARRO, PATH) "+
					 "VALUES (?, ?);";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, foto.getId_Carro());
			pst.setString(2, foto.getPath());					
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}
	}
	
	public void saveCarro(Carro carro) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "INSERT INTO Carro " +		
				     "(MODELO, COR, DATAENTRADA, FK_MONTADORA) "+
					 "VALUES (?,?,?,?);";
		try {
			java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(carro.getDataEntrada());
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, carro.getModelo());
			pst.setString(2, carro.getCor());
			pst.setDate(3, sqlDate);
			pst.setInt(4, carro.getMontadora().getId());
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally
		{
			con.close();			
		}
	}
	
	public void editCarro(Carro carro) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "UPDATE Carro " +
					 "SET MODELO = ?, COR = ?, DATAENTRADA = ?, FK_MONTADORA = ? "+
					 "WHERE ID_CARRO = ?;";
		try {
			java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(carro.getDataEntrada());
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, carro.getModelo());
			pst.setString(2, carro.getCor());
			pst.setDate(3, sqlDate);
			pst.setInt(4, carro.getMontadora().getId());
			pst.setInt(5, carro.getId());
			
			pst.execute();	
			pst.close();			
			
		} catch (SQLException e) {
			throw new SQLException();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally
		{
			con.close();			
		}		
	}

	public void editMontadora(Montadora mont) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "UPDATE Montadora " +
					 "SET NOME = ? "+
					 "WHERE ID_MONTADORA = ?;";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setString(1, mont.getNome());
			pst.setInt(2, mont.getId());
			
			pst.execute();	
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}		
	}
	
	public void deleteFoto(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "DELETE FROM Foto " +
				 	 "WHERE ID_FOTO = ?;";
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}	
	}

	public void deleteAllFotosFromCarro(int id) throws SQLException {
		con = new ConnectionFactory().getConnection();
		String sql = "DELETE FROM Foto " +
				 	 "WHERE FK_CARRO = ?;";
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}			
	}
	
	public void deleteCarro(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "DELETE FROM Carro " +
				 	 "WHERE ID_CARRO = ?;";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}			
	}
	
	public void deleteMontadora(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "DELETE FROM Montadora " +
				 	 "WHERE ID_MONTADORA = ?;";
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			pst.execute();			
			
			pst.close();
		} catch (SQLException e) {
			throw new SQLException();
		} finally
		{
			con.close();			
		}			
	}
	
	public List<Montadora> getMontadoras() throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT * FROM Montadora;";
		List<Montadora> lista = new ArrayList<>();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				Montadora mont = new Montadora();
				mont.setId(rs.getInt("ID_MONTADORA"));
				mont.setNome(rs.getString("NOME"));
				
				lista.add(mont);
			}
			rs.close();
			pst.close();
		}catch(SQLException e){
			throw new SQLException();
		}
		finally
		{
			con.close();
		}
		return lista;
	}
	
	public List<Carro> getCarros() throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT ID_CARRO, MODELO, COR, DATAENTRADA, ID_MONTADORA, NOME FROM Carro " +
					 "INNER JOIN Montadora "+
					 "ON Carro.FK_MONTADORA=Montadora.ID_MONTADORA;";
		List<Carro> lista = new ArrayList<>();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				Carro c = new Carro();
				c.setId(rs.getInt("ID_CARRO"));
				c.setModelo(rs.getString("MODELO"));
				c.setCor(rs.getString("COR"));
				c.setDataEntrada(rs.getDate("DATAENTRADA").toString());
				c.setMontadora(new Montadora(rs.getInt("ID_MONTADORA"),rs.getString("NOME")));
				
				lista.add(c);
			}
			rs.close();
			pst.close();
			return lista;
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			con.close();
		}
	}
	
	public List<Carro> getCarrosPorMontadora(int id_montadora) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT ID_CARRO FROM Carro " +
					 "WHERE FK_MONTADORA = ?";
		List<Carro> lista = new ArrayList<>();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id_montadora);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next())
			{
				Carro c = new Carro();
				c.setId(rs.getInt("ID_CARRO"));
				
				lista.add(c);
			}
			rs.close();
			pst.close();
			return lista;
		} catch(SQLException e)
		{
			throw new SQLException();
		} finally {
			con.close();
		}
	}
	
	public List<Foto> getFotos(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT ID_FOTO, PATH FROM Foto "+
					 "WHERE FK_CARRO = ?;";
		List<Foto> lista = new ArrayList<>();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				Foto foto = new Foto();
				foto.setId(rs.getInt("ID_FOTO"));
				foto.setPath(rs.getString("PATH"));
				foto.setId_Carro(id);
				
				lista.add(foto);
			}
			rs.close();
			pst.close();
			
			return lista;
		}catch(SQLException e){
			throw new SQLException();
		}
		finally
		{
			con.close();
		}
	}
	
	public Carro findCarro(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT ID_CARRO, MODELO, COR, DATAENTRADA, ID_MONTADORA, NOME FROM Carro " +
				 "INNER JOIN Montadora "+
				 "ON Carro.FK_MONTADORA=Montadora.ID_MONTADORA "+
				 "WHERE ID_CARRO = ?;";
		Carro c = new Carro();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				c.setId(rs.getInt("ID_CARRO"));
				c.setModelo(rs.getString("MODELO"));
				c.setCor(rs.getString("COR"));
				c.setDataEntrada(rs.getDate("DATAENTRADA").toString());
				c.setMontadora(new Montadora(rs.getInt("ID_MONTADORA"),rs.getString("NOME")));
			}
			rs.close();
			pst.close();
			return c;
		} catch(SQLException e)
		{
			throw new SQLException();
		}finally
		{
			con.close();
		}
	}
	
	public Montadora findMontadora(int id) throws SQLException
	{
		con = new ConnectionFactory().getConnection();
		String sql = "SELECT * FROM Montadora " +
					 "WHERE ID_MONTADORA = ?;";
		Montadora m = new Montadora();
		
		try {
			PreparedStatement pst = con.prepareStatement(sql);
			pst.setInt(1, id);
			
			ResultSet rs = pst.executeQuery();
			while(rs.next())
			{
				m.setId(rs.getInt("ID_MONTADORA"));
				m.setNome(rs.getString("NOME"));
			}
			rs.close();
			pst.close();
			return m;
		} catch(SQLException e)
		{
			throw new SQLException();
		}finally
		{
			con.close();
		}
	}
}