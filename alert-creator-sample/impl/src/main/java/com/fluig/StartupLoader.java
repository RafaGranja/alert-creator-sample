package com.fluig;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.totvs.technology.foundation.common.exception.FDNException;
import com.totvs.technology.foundation.security.service.SecurityService;
import com.totvs.technology.foundation.security.vo.FDNTenantVO;

/**
 * Esta classe cria o novo evento de notificações em todos os tenants
 * automaticamente quando feito deploy no servidor do Fluig.
 * 
 * OBS: Este processo é opcional. O evento pode ser registrado na Central de Notificações
 * a qualquer momento.
 * 
 * @author caio.psousa@fluig.com
 */
@Startup
@Singleton
public class StartupLoader {

	private transient Logger LOG = LoggerFactory.getLogger(StartupLoader.class);
	
	@EJB(lookup = SecurityService.JNDI_REMOTE_NAME)
	private SecurityService svcSecurity;

	@EJB
	private EventRegister eventRegister;

	@PostConstruct
	private void startup() {
		
		try {
			
			final List<FDNTenantVO> tenants = this.svcSecurity.findTenants();
			for (FDNTenantVO tenant : tenants) {
				eventRegister.registerEvent(tenant.getId());
			}
			
		} catch (FDNException ex) {
			LOG.error("Could not register papc event: " + ex.getMessage());
		}

	}

}
