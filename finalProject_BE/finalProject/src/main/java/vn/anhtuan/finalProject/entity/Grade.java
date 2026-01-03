package vn.anhtuan.finalProject.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {

    @Id
    // Lưu ý: Trong SQL id là TINYINT, Java dùng Integer là an toàn nhất
    private Integer id;

    @Column(nullable = false, length = 20)
    private String name; // Ví dụ: "Khối 6"

    @Column(nullable = false, length = 10, unique = true)
    private String code; // Ví dụ: "K6"

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}