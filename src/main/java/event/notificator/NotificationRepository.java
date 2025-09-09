package event.notificator;

import event.notificator.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("UPDATE NotificationEntity n SET n.isRead = true WHERE n.id IN :ids AND n.userId = :userId")
    void markAsReadByIdsAndUserId(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.createdAt < :cutoffDate")
    void deleteNotificationsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

    default void deleteNotificationsOlderThan7Days() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
        deleteNotificationsOlderThan(cutoffDate);
    }
}
