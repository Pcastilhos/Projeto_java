package dao;

import java.util.List;

// interface genérica com as operações de CRUD que todo DAO precisa ter
public interface Dao<T> {

    void salvar(T objeto);

    List<T> listarTodos();

    T buscarPorId(int id);

    void atualizar(T objeto);

    void excluir(int id);
}
