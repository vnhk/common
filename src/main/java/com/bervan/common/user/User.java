package com.bervan.common.user;

import com.bervan.common.model.BervanOwnedBaseEntity;
import com.bervan.common.model.PersistableTableOwnedData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User extends BervanOwnedBaseEntity<UUID> implements PersistableTableOwnedData<UUID>, UserDetails {
    @Id
    private UUID id;
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String dataCipherPassword;
    private String role;
    private boolean mainAccount = true;
    private boolean lockedAccount = false;
    private Boolean deleted = false;
    private LocalDateTime modificationDate;
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<UserToUserRelation> childrenRelations = new HashSet<>();

    public void setLockedAccount(boolean lockedAccount) {
        this.lockedAccount = lockedAccount;
    }

    public boolean isLockedAccount() {
        return lockedAccount;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        if (deleted || lockedAccount) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (deleted || lockedAccount) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (deleted || lockedAccount) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isEnabled() {
        if (deleted || lockedAccount) {
            return false;
        }

        return true;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (role == null) {
            role = "ROLE_USER";
        }
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        return grantedAuthorities;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Boolean isDeleted() {
        return deleted;
    }


    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String getTableFilterableColumnValue() {
        return username;
    }

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isMainAccount() {
        return mainAccount;
    }

    public void setMainAccount(boolean mainAccount) {
        this.mainAccount = mainAccount;
    }

    public Set<UserToUserRelation> getChildrenRelations() {
        return childrenRelations;
    }

    public void setChildrenRelations(Set<UserToUserRelation> userRelations) {
        this.childrenRelations = userRelations;
    }

    /**
     * Identity by {@link #id} only. Do not include collections or associations — Hibernate calls
     * hashCode/equals while loading persistent collections; touching lazy/eager sets here causes
     * {@code AssertionFailure: force initializing collection loading}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    public String getDataCipherPassword() {
        return dataCipherPassword;
    }

    public void setDataCipherPassword(String dataCipherPassword) {
        this.dataCipherPassword = dataCipherPassword;
    }
}
