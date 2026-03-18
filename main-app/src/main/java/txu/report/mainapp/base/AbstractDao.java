package txu.report.mainapp.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractDao<T> {

    @PersistenceContext
    private EntityManager entityManager;
    private final Class<T> entityClass;

    public AbstractDao() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional
    public void persist(T entity) {
        getEntityManager().persist(entity);
    }

    protected T merge(T entity) {
        return getEntityManager().merge(entity);
    }

    protected void remove(T entity){
        entity = merge(entity);
        getEntityManager().remove(entity);
    }

    protected List<T> getRessultList(Query query) {
        return query.getResultList();
    }

    protected T getSingle(Query query) {
        List<T> list = getRessultList(query);
        if (list.size() != 0) {
            return list.get(0);
        }
        return null;
    }

    protected T findById(Object Id) {
        return getEntityManager().find(entityClass, Id);
    }
}
