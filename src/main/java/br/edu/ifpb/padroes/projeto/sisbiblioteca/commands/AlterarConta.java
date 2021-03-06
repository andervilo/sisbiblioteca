/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpb.padroes.projeto.sisbiblioteca.commands;

import br.com.caelum.stella.validation.InvalidStateException;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.commands.Command;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.entities.Endereco;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.entities.Usuario;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.enums.TipoUsuario;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.model.UsuarioBo;
import br.edu.ifpb.padroes.projeto.sisbiblioteca.utils.DateUtils;
import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kieckegard
 */
public class AlterarConta implements Command {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        //Pessoa
        String cpf = request.getParameter("cpf");
        String nome = request.getParameter("nome");
        LocalDate dataNascimento = DateUtils.fromBrazilPattern(request.getParameter("dataNascimento"));

        //Usuario
        String matricula = request.getParameter("matricula");
        String senha = request.getParameter("senha");

        //Endereço
        Integer id = Integer.parseInt(request.getParameter("idEndereco"));
        String pais = request.getParameter("pais");
        String estado = request.getParameter("estado");
        String cidade = request.getParameter("cidade");
        String bairro = request.getParameter("bairro");
        String rua = request.getParameter("rua");
        int numero = Integer.valueOf(request.getParameter("numero"));
        Endereco endereco = new Endereco(pais, estado, cidade, bairro, rua, numero);
        endereco.setId(id);
        
        Usuario usuario = new Usuario(cpf,nome,dataNascimento,matricula,senha,TipoUsuario.BIBLIOTECARIO);
        usuario.setEndereco(endereco);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("minhaconta.jsp");
        
        UsuarioBo bo = new UsuarioBo();
        
        try {
            
            bo.atualizarConta(usuario);
            request.setAttribute("success", true);
            request.setAttribute("userChanged", true);
            request.setAttribute("userName", nome);
            dispatcher = request.getRequestDispatcher("login.jsp");
            
        } catch(InvalidStateException ex) {
            
            request.setAttribute("success", false);
            request.setAttribute("errorMsg", "O CPF inserido é inválido!");
            
        } finally {
            
            dispatcher.forward(request, response);
        }
        
        
    }
}
