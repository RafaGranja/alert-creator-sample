package com.fluig;

import javax.ejb.Remote;


@Remote
public interface PaPcAlertCreator {

	String JNDI_NAME = "pcp/PaPcAlertCreator";
	String JNDI_REMOTE_NAME = "java:global/alertcreatorsample/alert-creator-sample-impl/" + JNDI_NAME;
	
	/**
	 * Envia notificação de holerit disponível para todos os usuários do tenant.
	 * 
	 * @param tenantId - id do tenant.
	 */
	void sendPaPcAlert(Long tenantId);
	
}
