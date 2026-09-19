package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; //acesso aos codigos de status padrão do protocolo HTTP (ex: OK par 200)
import org.springframework.http.ResponseEntity;//resposta HTTP completa, retorna tanto corpo (JSON) quanto cbeçalhos e código de status HTTP.
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.modelo.ClienteSelecionador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/cliente")
public class ClienteControle {
	@Autowired
	private ClienteRepositorio repositorio;
	@Autowired
	private ClienteSelecionador selecionador;

	//obter cliente por id, no codigo original a url direcionava /cliente/cliente/id
	//tbm adiciona status http not found e ok
	@GetMapping("/{id}")
	public ResponseEntity<Cliente> obterCliente(@PathVariable long id) {
		List<Cliente> clientes = repositorio.findAll();
		Cliente cliente = selecionador.selecionar(clientes, id);
		if(cliente == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(cliente, HttpStatus.OK);
	}
//obtem todos os clientes /cliente/clientes
	@GetMapping("/clientes")
	public ResponseEntity<List<Cliente>> obterClientes() {
		List<Cliente> clientes = repositorio.findAll();
		if (clientes.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND)
		}
		return new ResponseEntity<>(clientes,HttpStatus.OK);
	}
	
	//cadastra cliente /cliente/cadastro
	@PostMapping("/cadastro")
    public ResponseEntity<?> cadastrarCliente(@RequestBody Cliente cliente) {
        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        repositorio.save(cliente);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
	
	//atualiza cliente /cliente/atualizar
	@PutMapping("/atualizar")
    public ResponseEntity<?> atualizarCliente(@RequestBody Cliente atualizacao) {
        if (atualizacao == null || atualizacao.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Cliente cliente = repositorio.findById(atualizacao.getId()).orElse(null);
        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ClienteAtualizador atualizador = new ClienteAtualizador();
        atualizador.atualizar(cliente, atualizacao);
        repositorio.save(cliente);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	//exclui clinete por id /cliente/excluir/{id}
	@DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> excluirCliente(@PathVariable long id) {
        Cliente cliente = repositorio.findById(id).orElse(null);
        if (cliente == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        repositorio.delete(cliente);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
