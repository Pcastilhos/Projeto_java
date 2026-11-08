package exception;

// exceção lançada quando uma regra de negócio é desrespeitada
public class NegocioException extends Exception {

    public NegocioException(String mensagem) {
        super(mensagem);
    }
}
