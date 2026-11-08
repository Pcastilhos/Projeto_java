package hotelapi.exception;

// exceção lançada quando uma regra de negócio é desrespeitada
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
