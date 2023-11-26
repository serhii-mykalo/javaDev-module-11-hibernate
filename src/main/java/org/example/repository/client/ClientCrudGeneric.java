package org.example.repository.client;

import org.example.entities.Client;
import org.example.entities.Ticket;
import org.example.hibernate.HibernateUtils;
import org.example.repository.GenericDao;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Objects;

public class ClientCrudGeneric implements GenericDao<Client> {

    private static final String GET_ALL_CLIENT_QUERY = "FROM Client";

    @Override
    public boolean create(Client client) {
        boolean result = false;

        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                client.setId(null);
                session.persist(client);
                transaction.commit();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                transaction.rollback();
            }
        }

        return result;
    }

    @Override
    public boolean update(Client client) {
        boolean result = false;

        if (Objects.isNull(client.getId())) {
            return false;
        }

        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.merge(client);
                transaction.commit();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
                transaction.rollback();
            }
        }

        return result;
    }

    @Override
    public Client getById(Object id) {
        Long clientId = (Long) id;

        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            Client client = session.get(Client.class, clientId);

            client.getTickets().forEach(Ticket::getId);

            return  client;
        }
    }

    @Override
    public List<Client> getAll() {
        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            return session.createQuery(GET_ALL_CLIENT_QUERY, Client.class).list();
        }
    }

    @Override
    public void deleteById(Object id) {
        Long clientId = (Long) id;

        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client existing = session.get(Client.class, clientId);
            session.remove(existing);
            transaction.commit();
        }
    }

    @Override
    public void delete(Client client) {
        try (Session session = HibernateUtils.getInstance().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(client);
            transaction.commit();
        }
    }
}