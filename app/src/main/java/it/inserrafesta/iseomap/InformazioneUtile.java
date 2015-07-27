package it.inserrafesta.iseomap;

/**
 * Created by Nicolo on 27/07/2015.
 */
public class InformazioneUtile {
    private String nome,indirizzo,telefono;
    public InformazioneUtile(String _nome,String _indirizzo,String _telefono){
        nome = _nome;
        indirizzo = _indirizzo;
        telefono = _telefono;
    }
    public boolean exists(){
        if(!nome.equals("")||!indirizzo.equals("")||!telefono.equals(""))
            return true;
        return false;
    }
    public String getNome(){
        return nome;
    }
    public String getIndirizzo(){
        return indirizzo;
    }
    public String getTelefono(){
        return telefono;
    }
}
