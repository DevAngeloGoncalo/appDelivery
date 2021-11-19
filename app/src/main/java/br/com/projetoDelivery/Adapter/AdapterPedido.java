package br.com.projetoDelivery.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.projetoDelivery.Model.ItemPedido;
import br.com.projetoDelivery.Model.Pedido;
import br.com.projetoDelivery.R;


public class AdapterPedido extends RecyclerView.Adapter<AdapterPedido.MyViewHolder> {

    private List<Pedido> pedidos;

    public AdapterPedido(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pedidos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {

        Pedido pedido = pedidos.get(i);
        holder.nome.setText(pedido.getNome());
        holder.cep.setText("CEP: " + pedido.getCEP());
        holder.cidade.setText("Cidade: " + pedido.getCidade());
        holder.bairro.setText("Bairro:" + pedido.getBairro());
        holder.endereco.setText("Endereço: "+ pedido.getEndereco());
        holder.numeroComplemento.setText("Endereço: "+ pedido.getNumeroEndereco() + " "+ pedido.getComplemento());
        holder.numeroContato.setText("Endereço: "+ pedido.getNumeroContato());

        if (pedido.getObservacao().isEmpty()){
            holder.observacao.setText("Obs: Sem observações");
        }else{
            holder.observacao.setText("Obs: "+ pedido.getObservacao());
        }

        List<ItemPedido> itens = new ArrayList<>();
        itens = pedido.getItens();
        String descricaoItens = "";

        int numeroItem = 1;
        Double total = 0.0;
        for( ItemPedido itemPedido : itens ){

            int qtde = itemPedido.getQuantidade();
            Double preco = itemPedido.getPreco();
            total += (qtde * preco);

            String nome = itemPedido.getNomeProduto();
            descricaoItens += numeroItem + ") " + nome + " / (" + qtde + " x R$ " + preco + ") \n";
            numeroItem++;
        }
        descricaoItens += "Total: R$ " + total;
        holder.itens.setText(descricaoItens);

        int metodoPagamento = pedido.getMetodoPagamento();
        String pagamento = metodoPagamento == 0 ? "Dinheiro" : "Máquina cartão" ;
        holder.pgto.setText( "pgto: " + pagamento );

    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView cep;
        TextView cidade;
        TextView bairro;
        TextView endereco;
        TextView numeroComplemento;
        TextView numeroContato;
        TextView pgto;
        TextView observacao;
        TextView itens;

        public MyViewHolder(View itemView) {
            super(itemView);

            nome        = itemView.findViewById(R.id.textPedidoNome);
            cep        = itemView.findViewById(R.id.textCEP);
            cidade    = itemView.findViewById(R.id.textPedidoEndereco);
            bairro    = itemView.findViewById(R.id.textPedidoBairro);
            endereco    = itemView.findViewById(R.id.textPedidoEndereco);
            numeroComplemento    = itemView.findViewById(R.id.textPedidoNumeroComplemento);
            numeroContato    = itemView.findViewById(R.id.textPedidoNumeroContato);
            pgto        = itemView.findViewById(R.id.textPedidoPgto);
            observacao  = itemView.findViewById(R.id.textPedidoObs);
            itens       = itemView.findViewById(R.id.textPedidoItens);
        }
    }

}
