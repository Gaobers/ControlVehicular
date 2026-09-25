package org.esfe.repositorios;

import org.esfe.modelos.RecordatorioMantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRecordatorioMantenimientoRepository extends JpaRepository<RecordatorioMantenimiento, Long> {
}