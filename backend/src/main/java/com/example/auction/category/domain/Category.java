package com.example.auction.category.domain;

import com.example.auction.util.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    private int depth;

    private int priority;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @OrderBy("priority ASC")
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    // 연관관계 메서드
    public void addChild(Category child) {
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.parent = null;
    }

    // 이름 변경
    public void changeName(String newName) {
        this.name = newName;
    }

    // 부모 변경
    public void changeParent(Category newParent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = newParent;
        this.depth = (newParent != null) ? newParent.getDepth() + 1 : 0;
        if (newParent != null && !newParent.getChildren().contains(this)) {
            newParent.getChildren().add(this);
        }
        // 하위 자식 depth 업데이트
        for (Category child : children) {
            child.updateDepthRecursively();
        }
    }

    private void updateDepthRecursively() {
        this.depth = (parent != null) ? parent.getDepth() + 1 : 0;
        for (Category child : children) {
            child.updateDepthRecursively();
        }
    }

    public void changePriority(int newPriority) {
        this.priority = newPriority;
    }


}
