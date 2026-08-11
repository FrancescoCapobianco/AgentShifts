package activity.ui.agentshifts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import System.Service.Objects.Flight;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<Flight> listaVoli;
    private Context context;

    public FlightAdapter(List<Flight> listaVoli, Context context) {
        this.listaVoli = listaVoli;
        this.context = context;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_volo, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight voloCorrente = listaVoli.get(position);

        // Imposta i testi
        holder.textCodiceVolo.setText(voloCorrente.getCodiceVolo());
        holder.textNomeCompagnia.setText(voloCorrente.getCompagnia());
        holder.textTrattaVolo.setText(voloCorrente.getLuogoArrivo() + " -> " + voloCorrente.getLuogoDestinazione());

        // Caricamento dinamico dell'immagine (La Magia!)
        String nomeFileImmagine = voloCorrente.getLogoDrawable();
        int imageResource = context.getResources().getIdentifier(nomeFileImmagine, "drawable", context.getPackageName());

        if (imageResource != 0)
            holder.imgLogoCompagnia.setImageResource(imageResource);
        else
            holder.imgLogoCompagnia.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public int getItemCount() {
        return listaVoli.size();
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {
        ImageView imgLogoCompagnia;
        TextView textCodiceVolo;
        TextView textNomeCompagnia;
        TextView textTrattaVolo;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLogoCompagnia = itemView.findViewById(R.id.imgLogoCompagnia);
            textCodiceVolo = itemView.findViewById(R.id.textCodiceVolo);
            textNomeCompagnia = itemView.findViewById(R.id.textNomeCompagnia);
            textTrattaVolo = itemView.findViewById(R.id.textTrattaVolo);
        }
    }
}