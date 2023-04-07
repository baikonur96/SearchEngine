package searchengine.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String status;
    @Column(name = "status_time")
    private LocalDateTime statusTime;
    @Column(name = "last_error")
    private String lastError;
    private String url;
    private String name;
}
