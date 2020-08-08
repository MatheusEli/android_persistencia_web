package br.com.alura.estoque.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.alura.estoque.R;
import br.com.alura.estoque.model.Produto;
import br.com.alura.estoque.repository.ProdutoRepository;
import br.com.alura.estoque.ui.dialog.EditaProdutoDialog;
import br.com.alura.estoque.ui.dialog.SalvaProdutoDialog;
import br.com.alura.estoque.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "Lista de produtos";
    private static final String MENSAGEM_ERRO_BUSCA_PRODUTOS = "Não foi possivel carregar os produtos novos";
    private static final String MENSAGEM_ERRO_REMOCAO = "Não foi possível remover o produto";
    private static final String MENSAGEM_ERRO_SALVA = "Não foi possível salvar o produto";
    private static final String MENSAGEM_ERRO_EDICAO = "Não foi possível editar";
    private ListaProdutosAdapter adapter;
    private ProdutoRepository produtoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        produtoRepository = new ProdutoRepository(this);
        buscaProdutos();
    }

    private void buscaProdutos() {
        produtoRepository.buscaProdutos(new ProdutoRepository.DadosCarregadosCallback<List<Produto>>() {
            @Override
            public void quandoSucesso(List<Produto> resultado) {
                adapter.atualiza(resultado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(MENSAGEM_ERRO_BUSCA_PRODUTOS);
            }
        });
    }

    private void mostraErro(String mensagem) {
        Toast.makeText(this,mensagem,
                Toast.LENGTH_SHORT).show();
    }

    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener((posicao, produtoEscolhido) -> {
            remove(posicao, produtoEscolhido);
        });
    }

    private void remove(int posicao, Produto produtoEscolhido) {
        produtoRepository.remove(produtoEscolhido,
                new ProdutoRepository.DadosCarregadosCallback<Void>() {
                    @Override
                    public void quandoSucesso(Void resultado) {
                        adapter.remove(posicao);
                    }

                    @Override
                    public void quandoFalha(String erro) {
                        mostraErro(MENSAGEM_ERRO_REMOCAO);
                    }
                });
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, produtoCriado -> salva(produtoCriado)).mostra();
    }

    private void salva(Produto produtoCriado) {
        produtoRepository.salva(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                adapter.adiciona(resultado);
            }

            @Override
            public void quandoFalha(String erro) {

                mostraErro(MENSAGEM_ERRO_SALVA);
            }
        });
    }

    private void abreFormularioEditaProduto(int posicao, Produto produto) {
        new EditaProdutoDialog(this, produto,
                produtoCriado -> edita(posicao, produtoCriado))
                .mostra();
    }

    private void edita(int posicao, Produto produtoCriado) {
        produtoRepository.edita(produtoCriado, new ProdutoRepository.DadosCarregadosCallback<Produto>() {
            @Override
            public void quandoSucesso(Produto resultado) {
                adapter.edita(posicao, resultado);
            }

            @Override
            public void quandoFalha(String erro) {
                mostraErro(MENSAGEM_ERRO_EDICAO);
            }
        });
    }

}
