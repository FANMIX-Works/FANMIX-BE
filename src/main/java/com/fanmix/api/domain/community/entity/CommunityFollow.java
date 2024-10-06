package com.fanmix.api.domain.community.entity;

import com.fanmix.api.domain.common.entity.BaseEntity;
import com.fanmix.api.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunityFollow extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_follow_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private Boolean followStatus = Boolean.FALSE;

    public CommunityFollow(Community community, Member member) {
        this.community = community;
        this.member = member;
    }

    public void changeFollowStatus() {
       if(followStatus == Boolean.FALSE) {
           followStatus = Boolean.TRUE;
       } else {
           followStatus = Boolean.FALSE;
       }
    }
}
