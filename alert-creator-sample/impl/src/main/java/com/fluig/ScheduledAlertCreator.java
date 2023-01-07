package com.fluig;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.totvs.technology.foundation.security.service.SecurityService;
import com.totvs.technology.foundation.security.vo.FDNTenantVO;

/**
 * Esta classe é responsável invocar o envio de notificações de holerit disponível
 * a cada 2 minutos.
 * 
 * OBS: Esta classe é apenas uma POC para exemplificar o envio de notificações.
 * Não é uma boa prática fazer deploy deste tipo de código em produção.
 * 
 * @author caio.psousa@fluig.com
 */
@Singleton
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ScheduledAlertCreator {

	@EJB(lookup = SecurityService.JNDI_REMOTE_NAME)
	private SecurityService svcSecurity;
	
	@EJB
	private PaPcAlertCreator holeritAlertCreator;

	/**
	 * Envia alertas de holerit disponível a cada 2 minutos.
	 */
	//@Schedule(minute ="*/2", hour = "*", dayOfMonth = "*", month = "*", year = "*")
	public void sendAlert() {
		
		final List<FDNTenantVO> tenants = this.svcSecurity.findTenants();
		for (FDNTenantVO tenant : tenants) {
			holeritAlertCreator.sendPaPCAlert(tenant.getId());
		}
		
	}

}
