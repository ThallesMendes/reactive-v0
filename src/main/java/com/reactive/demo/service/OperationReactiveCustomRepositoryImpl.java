package com.reactive.demo.service;

import com.reactive.demo.entity.OperationEntity;
import com.reactive.demo.entity.OperationPeriodEffectEntity;
import com.reactive.demo.entity.OperationTaxEntity;
import com.reactive.demo.mapping.ResultMapperRelations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class OperationReactiveCustomRepositoryImpl
    implements OperationReactiveCustomRepository<OperationEntity, UUID> {

  private R2dbcEntityTemplate template;
  private DatabaseClient client;

  public OperationReactiveCustomRepositoryImpl(R2dbcEntityTemplate template,
                                               DatabaseClient client) {
    this.template = template;
    this.client = client;
  }

  @Override
  public Mono<OperationEntity> findById(UUID uuid) {

    final var query = new StringBuilder();
    query.append("select o.operation_id, o.company_id, o.client_id, o.code, op.operation_period_effect_id, op.period_effect, op.consider_as_billing_gross_revenue, ");
    query.append("op.description, op.operation_type, ot.operation_tax_id, ot.parameter_period_effect_tax_id, ta.name as tax_name, ta.code as tax_code, ");
    query.append("ta.initials as tax_initials, osn.operation_tax_simples_nacional_id, osn.simples_nacional_attachment_id, osn.simples_nacional_activity_id, ");
    query.append("sna.description as attachment_name, sna.code as attachment_code, snc.description as activity_name, snc.code as activity_code from operation o ");
    query.append("inner join operation_period_effect op on op.operation_id = o.operation_id ");
    query.append("left join operation_tax ot on ot.operation_period_effect_id = op.operation_period_effect_id ");
    query.append("inner join parameter_period_effect_tax ppt on ppt.parameter_period_effect_tax_id = ot.parameter_period_effect_tax_id ");
    query.append("inner join tax ta on ta.tax_id = ppt.tax_id ");
    query.append("left join operation_tax_simples_nacional osn on osn.operation_tax_id = ot.operation_tax_id ");
    query.append("left join simples_nacional_attachment sna on sna.simples_nacional_attachment_id = osn.simples_nacional_attachment_id ");
    query.append("left join simples_nacional_activity snc on snc.simples_nacional_activity_id = osn.simples_nacional_activity_id ");
    query.append("where o.operation_id = :operationId");

    final var mapper = new OperationRowMapper();

    final var result = client.sql(query.toString())
        .bind("operationId", uuid);

    return result
        .map(mapper::apply)
        .all()
        .reduce((previous, current) -> {

          final var rowMapper = new ResultMapperRelations();

          return rowMapper.mapper(previous, current);

          /** var periodEffects = new ArrayList<>(previous.getPeriodEffects());

          final var periodEffect = current.getPeriodEffects().stream().findFirst().orElseThrow();

          periodEffects.stream()
              .filter(p -> p.getId().equals(periodEffect.getId()))
              .findFirst()
              .ifPresentOrElse(period -> {
                final var taxes = new ArrayList<>(period.getTaxes());
                taxes.addAll(periodEffect.getTaxes());
                period.setTaxes(new HashSet<>(taxes));

              },() -> periodEffects.addAll(current.getPeriodEffects()));

          previous.setPeriodEffects(periodEffects);
          return previous; */
        });
  }
}
