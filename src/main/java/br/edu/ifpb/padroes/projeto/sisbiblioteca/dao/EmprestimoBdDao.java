/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpb.padroes.projeto.sisbiblioteca.dao;

import br.edu.ifpb.padroes.projeto.sisbiblioteca.entities.Aluno;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.entities.Emprestimo;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.entities.Livro;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.enums.EstadoEmprestimoEnum;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kieckegard
 */
public class EmprestimoBdDao implements EmprestimoDao {

    private final Dao<Aluno, String> alunoDao;
    private final Dao<Livro, Long> livroDao;
    
    public EmprestimoBdDao() {
        alunoDao = FactoryProvider.createFactory(FactoryProvider.jdbc).getAlunoDao();
        livroDao = FactoryProvider.createFactory(FactoryProvider.jdbc).getLivroDao();
    }
    
    @Override
    public void finalizarEmprestimo(Emprestimo emprestimo) {
        String sql = "UPDATE emprestimo SET idEstado = ?, dataEntrega = ? WHERE id = ?";
        try {
            PreparedStatement pstm = FactoryConnection.createConnection().prepareStatement(sql);
            
            int i = 1;
            
            System.out.println("Estou mudando o estado de empréstimo para "+emprestimo.getEstadoValue());
            pstm.setInt(i++, emprestimo.getEstadoValue());
            pstm.setDate(i++, java.sql.Date.valueOf(emprestimo.getDataEntregue()));
            pstm.setInt(i++, emprestimo.getId());
            
            if(pstm.executeUpdate()>0)
                System.out.println("Alterei!!!");
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void add(Emprestimo obj) {
        String sql = "INSERT INTO emprestimo(alunoMatricula, livroIsbn, dataInicio, dataFim, idEstado)"
                + " VALUES(?,?,?,?,?) RETURNING id";
        
        try {
            PreparedStatement pstm = FactoryConnection.createConnection().prepareStatement(sql);
            
            int i=1;
            
            pstm.setString(i++, obj.getAluno().getMatricula());
            pstm.setLong(i++, obj.getLivro().getIsbn());
            pstm.setDate(i++, java.sql.Date.valueOf(obj.getStartDate()));
            pstm.setDate(i++, java.sql.Date.valueOf(obj.getEndDate()));
            pstm.setInt(i++, obj.getEstadoValue());
            
            ResultSet rs = pstm.executeQuery();
            
            if(rs.next())
                obj.setId(rs.getInt("id"));
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void rem(Emprestimo obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(Emprestimo obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Emprestimo get(Integer obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Emprestimo> list() {
        String sql = "SELECT * FROM emprestimo";
        List<Emprestimo> emprestimos = new LinkedList<>();
        
        try {
            PreparedStatement pstm = FactoryConnection.createConnection().prepareStatement(sql);
            
            ResultSet rs = pstm.executeQuery();
            
            while(rs.next()) {
                emprestimos.add(formaEmprestimo(rs));
            }
            
            return emprestimos;
            
        }
        catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        
        return emprestimos;
    }
    
    private Livro getLivroFromIsbn(Long isbn) {
        return livroDao.get(isbn);
    }
    
    private Aluno getAlunoFromMatricula(String matricula) {
        return alunoDao.get(matricula);
    }
    
    private Emprestimo formaEmprestimo(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String alunoMatricula = rs.getString("alunoMatricula");
        Long livroIsbn = rs.getLong("livroIsbn");
        LocalDate dataInicio = rs.getDate("dataInicio").toLocalDate();
        LocalDate dataFim = rs.getDate("dataFim").toLocalDate();
        Integer idEstado = rs.getInt("idEstado");
        
        EstadoEmprestimoEnum estadoEmprestimo = EstadoEmprestimoEnum.values()[idEstado];
        
        Date date = rs.getDate("dataEntrega");
        LocalDate dataEntrega = null;
        if(date != null) dataEntrega = date.toLocalDate();
        
        Livro livro = getLivroFromIsbn(livroIsbn);
        Aluno aluno = getAlunoFromMatricula(alunoMatricula);
        
        Emprestimo emprestimo = new Emprestimo(aluno,livro,dataInicio,dataFim,estadoEmprestimo);
        emprestimo.setDataEntrega(dataEntrega);
        
        return emprestimo;
    }
    
}
