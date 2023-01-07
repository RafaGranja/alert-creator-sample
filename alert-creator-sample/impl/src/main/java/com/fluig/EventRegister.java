package com.fluig;

import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import com.totvs.technology.foundation.alert.service.AlertEventService;
import com.totvs.technology.foundation.alert.service.AlertModuleService;
import com.totvs.technology.foundation.alert.vo.AlertModuleVO;
import com.totvs.technology.foundation.common.SystemConstants;
import com.totvs.technology.foundation.common.exception.FDNException;

/**
 * Esta classe é responsável por registrar o novo evento na Central de Notificações do Fluig.
 * 
 * @author caio.psousa@fluig.com
 *
 */
@RunAs(SystemConstants.TENANT_ADMIN_ROLE)
@Singleton(mappedName = "PaPcEventRegister", name = "PaPcEventRegister")
public class EventRegister {

	/**
	 * Este é um serviço da API de notificações do Fluig.
	 * É responsável por cadastrar na Central de Notificações um novo módulo de notificações.
	 */
	@EJB(lookup = AlertModuleService.JNDI_REMOTE_NAME)
	private AlertModuleService alertModuleService;
	
	/**
	 * Este é um serviço da API de notificações do Fluig.
	 * É responsável por cadastrar na Central de Notificações um novo evento de notificações.
	 */
	@EJB(lookup = AlertEventService.JNDI_REMOTE_NAME)
	private AlertEventService alertEventService;

	/**
	 * Registra o evento na Central de Notificações do Fluig.
	 * 
	 * @param tenantId - id do tenant para o qual o evento será registrado
	 */
	public void registerEvent(Long tenantId) throws FDNException {
		
		/*
		 * Cria um novo módulo de notificações. Os módulos padrão do Fluig são:
		 * 1. DOCUMENT
		 * 2. PROCESSES
		 * 3. PORTAL
		 * 4. COLABORATION
		 * 
		 * Caso o novo evento de notificações se encaixe em um destes módulos, 
		 * não é necessário criar um novo.
		 */
		final AlertModuleVO module = alertModuleService.registerModule(
				"PCP_MODULE", "Notificações do PCP", tenantId);
		
		/*
		 * Parâmetros necessários:
		 * 1. eventKey - String única que representa o evento no Fluig.
		 * 2. required - Indica se o evento é requerido. Caso seja, o usuário não conseguirá configurar para não receber
		 * alertas do evento.
		 * 3. descriptionKey - Descrição do evento. Pode ser um texto plano ou uma chave para obter descrição traduzida no I18n.
		 * 4. singleDescriptionKey - Descrição da ação feita por um usuário. Pode ser um texto plano ou uma chave para obter
		 * descrição traduzida no I18n.
		 * 5. groupDescriptionKey - Descrição da ação feita por por vários usuários. Pode ser um texto plano ou uma chave para obter
		 * a descrição traduzida no I18n.
		 * 6. eventIcon - Ícone que representa o evento (opcional)
		 * 7. moduleId - Módulo ao qual pertence este evento (Colab, Documentos, Processos, etc)
		 * 8. grouped - Indica se a notificação pode ser agrupada por ação e objeto.
		 * 9. canRemove - Indica se a notificação pode ser removida.
		 * 10. removeAfterExecAction - Indica se a notificação é removida após ser executada uma ação.
		 * 11. onlyAdmin - Indica se o evento é válido somente para usuários administradores.
		 * 12. tenantId - Tenant para o qual o evento está sendo cadastrado.
		 */
		alertEventService.createEvent(
				"PAPC_CREATED",
				Boolean.TRUE,
				"PAPC cadastrado, esperando programação",
				"Já está disponível",
				null,
				null,
				module.getId(),
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				Boolean.FALSE,
				tenantId);
		
	}

}
