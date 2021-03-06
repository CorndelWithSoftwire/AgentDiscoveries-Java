package org.softwire.training.db.daos;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.softwire.training.models.Agent;

import javax.inject.Inject;
import java.util.Optional;

public class AgentsDao {

    @Inject
    Jdbi jdbi;

    public Optional<Agent> getAgent(String callSign) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT * FROM agent WHERE call_sign = :call_sign")
                    .bind("call_sign", callSign)
                    .mapToBean(Agent.class)
                    .findFirst();
        }
    }

    public Optional<Agent> getAgentByUserId(int userId) {
        try (Handle handle = jdbi.open()) {
            return handle.createQuery("SELECT * FROM agent WHERE user_id = :user_id")
                    .bind("user_id", userId)
                    .mapToBean(Agent.class)
                    .findFirst();
        }
    }

    public int addAgent(Agent agent) {
        try (Handle handle = jdbi.open()) {
            return handle.createUpdate("INSERT INTO agent (first_name, last_name, date_of_birth, rank, call_sign, user_id)" +
                    " VALUES (:first_name, :last_name, :date_of_birth, :rank, :call_sign, :user_id)")
                    .bind("first_name", agent.getFirstName())
                    .bind("last_name", agent.getLastName())
                    .bind("date_of_birth", agent.getDateOfBirth())
                    .bind("rank", agent.getRank())
                    .bind("call_sign", agent.getCallSign())
                    .bind("user_id", agent.getUserId())
                    .execute();
        }
    }

    public int deleteAgentByUserId(int userId) {
        try (Handle handle = jdbi.open()) {
            return handle.createUpdate("DELETE FROM agent WHERE user_id = :user_id")
                    .bind("user_id", userId)
                    .execute();
        }
    }

    public int updateAgent(Agent agent) {
        try (Handle handle = jdbi.open()) {
            return handle.createUpdate("UPDATE agent SET first_name = :first_name, last_name = :last_name, " +
                    "date_of_birth = :date_of_birth, rank = :rank, call_sign = :call_sign " +
                    "WHERE user_id = :user_id")
                    .bind("user_id", agent.getUserId())
                    .bind("first_name", agent.getFirstName())
                    .bind("last_name", agent.getLastName())
                    .bind("date_of_birth", agent.getDateOfBirth())
                    .bind("rank", agent.getRank())
                    .bind("call_sign", agent.getCallSign())
                    .execute();
        }
    }
}
