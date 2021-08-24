package com.reactive.demo.entity;

import com.reactive.demo.mapping.Entity;
import java.util.UUID;

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
@Table("operation_tax")
public class OperationTaxEntity implements Entity<UUID> {

	@Id
	@Column("operation_tax_id")
	private UUID id;

	@Column("operation_period_effect_id")
	private UUID operationPeriodEffectId;

	@Column("company_id")
	private UUID companyId;

	@Column("parameter_period_effect_tax_id")
	private UUID parameterPeriodEffectTaxId;

	@Transient
	private String taxName;

	@Transient
	private String taxInitials;

	@Transient
	private String taxCode;

	@MappedCollection(idColumn = "operation_tax_id")
	private OperationTaxSimplesNacionalEntity operationTaxSimplesNacional;

	@Transient
	private boolean isNew;

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

}
