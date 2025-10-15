package com.example.auction.category.domain;

import com.example.auction.util.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE) // 부모 노드가 삭제됐을 때만
    @OrderBy("priority ASC")
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    // 연관관계 메서드
    // 부모 변경
    public void changeParent(Category newParent) {
        // 변경되지 않았으면 아무것도 안함
        if (this.parent == newParent) {
            return;
        }

        // 기존 부모와의 관계 제거
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }

        // 새로운 부모 설정
        this.parent = newParent;
        this.depth = (newParent != null) ? newParent.getDepth() + 1 : 0;

        // 새로운 부모의 children에 추가
        if (newParent != null) {
            newParent.getChildren().add(this);
        }

        // 하위 자식 depth 업데이트
        for (Category child : children) {
            child.updateDepthRecursively();
        }
    }

    // 부모 카테고리 변경시 depth는 자동 계산
    private void updateDepthRecursively() {
        this.depth = (parent != null) ? parent.getDepth() + 1 : 0;
        for (Category child : children) {
            child.updateDepthRecursively();
        }
    }

    // 이름 변경
    public void changeName(String newName) {
        this.name = newName;
    }

    public void changePriority(int newPriority) {
        this.priority = newPriority;
    }


}
