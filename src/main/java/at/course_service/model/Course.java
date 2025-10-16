package at.course_service.model;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Table(name = "courses") //Table name in the database
@Entity //Hibernate entity
public class Course {
    @Id //Primary Key Hibernate
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto Increment
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;
    @Column(name = "duration", nullable = false)
    private long duration; // duration in min
    @Column(name = "difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    @CreationTimestamp
    @DateTimeFormat(pattern = DateUtil.DATE_FORMAT)
    private LocalDateTime updatedAt;
    @Type(JsonBinaryType.class)
    @Column(name = "module_collection", columnDefinition = "jsonb")
    private ModuleCollection moduleCollection;

    //replace equals and hashcode - since the entities fetched from different methods and separate transactions,
    //they represent different objects in memory
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getEntityClass(this) != getEntityClass(o)) return false;
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public final int hashCode() {
        return getEntityClass(this).hashCode();
    }

    //if the object is a Hibernate proxy, get the underlying class, otherwise return Object condition ? valueIfTrue : valueIfFalse
    //Hibernate.getClass() doesnt work producing side effects: https://jpa-buddy.com/blog/hopefully-the-final-article-about-equals-and-hashcode-for-jpa-entities-with-db-generated-ids/
    public static Class<?> getEntityClass(Object o) {
        return o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                o.getClass();
    }
}
