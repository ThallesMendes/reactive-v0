package com.reactive.demo.entity;

import com.reactive.demo.mapping.Entity;
import com.reactive.demo.mapping.OneToMany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lombok.Singular;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("operation")
public class OperationEntity implements Entity<UUID> {

	@Transient
	private boolean isNew;

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Id
	@Column("operation_id")
	private UUID id;

	@Column("company_id")
	private UUID companyId;

	@Column("client_id")
	private UUID clientId;

	@Column("code")
	private String code;

	@OneToMany
	@MappedCollection(idColumn = "operation_id")
	@Singular
	private List<OperationPeriodEffectEntity> periodEffects = new ArrayList<>();
}
