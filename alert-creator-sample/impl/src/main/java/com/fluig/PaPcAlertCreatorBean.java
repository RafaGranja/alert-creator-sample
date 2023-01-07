package com.fluig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.totvs.technology.foundation.alert.AlertAction;
import com.totvs.technology.foundation.alert.AlertObject;
import com.totvs.technology.foundation.alert.GenericAlertAction;
import com.totvs.technology.foundation.alert.GenericAlertObject;
import com.totvs.technology.foundation.alert.enumeration.FDNAlertActionType;
import com.totvs.technology.foundation.alert.enumeration.FDNAlertIntegrationType;
import com.totvs.technology.foundation.alert.service.AlertService;
import com.totvs.technology.foundation.common.exception.FDNException;
import com.totvs.technology.foundation.security.service.SecurityService;
import com.totvs.technology.foundation.security.vo.FDNUserVO;

/**
 * Esta é a classe que será chamada pela Central de Notificações para enviar
 * alertas por este aplicativo.
 * 
 * Esta classe deve, obrigatoriamente, implemantar a interface "com.totvs.technology.foundation.alert.sender.AlertSenderApp",
 * que faz parte da API da Central de Notificações do Fluig.
 * 
 * @author caio.psousa@fluig.com
 *
 */
@Stateless(name = PaPcAlertCreator.JNDI_NAME, mappedName = PaPcAlertCreator.JNDI_NAME)
public class PaPcAlertCreatorBean implements PaPcAlertCreator {
	
	private static final Logger LOG = LoggerFactory.getLogger(PaPcAlertCreatorBean.class);

	/**
	 * Este serviço faz parte da API de Notificações do Fluig.
	 * Ela pode ser utilizada para enviar um alerta.
	 */
	@EJB(lookup = AlertService.JNDI_REMOTE_NAME)
	private AlertService alertService;
	
	@EJB(lookup = SecurityService.JNDI_REMOTE_NAME)
	private SecurityService svcSecurity;

	@EJB(lookup = GroupService.JNDI_REMOTE_NAME)
	private GroupService groupService;

	/**
	 * Este método é onde se encontra a implementação necessária para envio
	 * de um alerta para a central de notificações.
	 * 
	 * @throws FDNException 
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void sendPaPcAlert(final Long tenantId, final String _group) {
		
		LOG.info("Enviando notificações de papc cadastrado pela engenharia.");
		final List<FDNUserVO> users = svcSecurity.findActiveUsersByTenant(tenantId);
		final List<ColleagueVO> users2 = groupService.findUsersByGroup(_group);
		
		for (ColleagueVO user : users2) {
			
			/* Login do usuário que irá receber a notificação */
			final String aliasReceiver = user.getLogin();
			
			/* Abstração do objeto relacionado à notificação. Neste caso, um PAPC */
			final AlertObject PAPC = new GenericAlertObject(
					user.getId(), 
					"com.fluig.PAPC",
					"o seu",
					"PAPC",
					null,
					"/social/" + user.getLogin());
			
			/* Abstração do lugar onde a notificação foi gerada. Neste caso, o sistema RH Online. */
			final AlertObject rhOnline = new GenericAlertObject(
					user.getId(), 
					"com.fluig.PCP",
					null,
					"PCP",
					null,
					"/home");

			/* Uma ação disponibilizada pelo alerta */
			final List<AlertAction> actions = new ArrayList<>();
			
			final AlertAction accept = new GenericAlertAction(
					"GO_PLANO_DE_CORTE",
					FDNAlertActionType.MAIN,
					"Visualizar",
					null,
					FDNAlertIntegrationType.NONE,
					null,
					"/home");
			
			actions.add(accept);
			
			/*
			 * Metadados da notificação.
			 * Posso popular este Map para eventualmente ser
			 * utilizado por um aplicativo de envio de notificações.
			 */
			final Map<String, String> metadata = new HashMap<>();
			
			metadata.put("myKey1", "myData1");
			metadata.put("myKey2", "myData2");
			metadata.put("myKey3", "myData3");
			
			/* 
			 * Envia a notificação para a Central de Notificações
			 * 
			 * Parâmetros:
			 * 1. eventKey - Chave do evento cadastrado para envio de notificações
			 * 2. loginSender - login do usuário que envia a notificação. (opcional)
			 * 3. loginReceiver - login do usuário que irá receber a notificação
			 * 4. object - objeto associado à notificação (opcional)
			 * 5. place - lugar onde a notificação foi gerada (opcional)
			 * 6. actions - ações disponibilizadas pela notificação (opcional)
			 * 7. metadata - metadados da notificação (opcional) 
			 * 
			 */
			try {
				alertService.sendAlert("HOLERIT_WARNNING", null, aliasReceiver, PAPC, rhOnline, actions, metadata);
			} catch (FDNException e) {
				LOG.error("Erro ao enviar notificações HOLERIT_WARNNING: ", e);
			}
			
			LOG.info("Notificações HOLERIT_WARNNING enviadas!");
			
		}
		
	}

}
