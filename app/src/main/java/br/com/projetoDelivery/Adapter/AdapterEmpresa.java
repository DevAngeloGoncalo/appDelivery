package br.com.projetoDelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.projetoDelivery.Model.Empresa;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.projetoDelivery.R;


public class AdapterEmpresa extends RecyclerView.Adapter<AdapterEmpresa.MyViewHolder> {

    private List<Empresa> empresas;

    public AdapterEmpresa(List<Empresa> empresas) {
        this.empresas = empresas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_empresa, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Empresa empresa = empresas.get(i);
        holder.nomeEmpresa.setText(empresa.getNomeFantasia());
        holder.categoria.setText(empresa.getEspecialidade().toUpperCase() + " | ");
        holder.telefone.setText(empresa.getTelefone());

        //Carregar imagem
        String urlImagem = empresa.getUrlImagem();
        Picasso.get().load( urlImagem ).into( holder.imagemEmpresa );

    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imagemEmpresa;
        TextView nomeEmpresa;
        TextView categoria;
        TextView telefone;


        public MyViewHolder(View itemView) {
            super(itemView);

            nomeEmpresa = itemView.findViewById(R.id.textNomeEmpresa);
            categoria = itemView.findViewById(R.id.textCategoriaEmpresa);
            telefone = itemView.findViewById(R.id.textTelefone);
            imagemEmpresa = itemView.findViewById(R.id.imageEmpresa);
        }
    }
}
