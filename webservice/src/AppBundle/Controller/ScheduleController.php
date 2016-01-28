<?php

namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\Validator\Constraints\DateTime;
use Symfony\Component\HttpFoundation\Request;

use AppBundle\Entity\Schedule;
use AppBundle\Response\ServiceResponse;

class ScheduleController extends Controller
{
    /**
	 * @Route("/schedules", name="schedule_all")
     * @Method({"GET", "HEAD"})
     */
    public function allAction()
    {
    	$repository = $this->getDoctrine()
			->getRepository('AppBundle:Schedule');
			
		$schedules = $repository->findAll();
		
		return new ServiceResponse($schedules);
    }
    
     /**
     * @Route("/schedules/{id}", name="schedule_show", requirements={
     *     "id": "\d+"
     * })
     * @Method({"GET", "HEAD"})
     */
    public function showAction($id){
        $schedule = $this->getDoctrine()
            ->getRepository('AppBundle:Schedule')
            ->find($id);
            
        if (!$schedule) {
            $mess = "schedule(id=$id) not found !";
            return new ServiceResponse(null, false, $mess);
        }
        
        return new ServiceResponse($schedule);
    }
    
    /**
     * @Route("/schedule/create", name="schedule_create")
     * @Method({"POST", "PUT"})
     */
    public function createAction(Request $request){
            
        $schedule = new Schedule();
        if ($request->request->get("start_time") != null) {
            $schedule->setStartTime(new \DateTime($request->request->get("start_time")));
        }
        
        if ($request->request->get("end_time") != null) {
            $schedule->setEndTime(new \DateTime($request->request->get("end_time")));
        }
        
        $schedule->setMessage($request->request->get("message"));
        
        $validator = $this->get('validator');
        $errors = $validator->validate($schedule);
        
        if (count($errors) > 0) {
            
            $mess = ServiceResponse::messageFromValidateErrorList($errors);
            
            return new ServiceResponse($schedule, false, $mess);
        }
        
        $em = $this->getDoctrine()->getManager();
        $em->persist($schedule);
        $em->flush();
        
        return new ServiceResponse($schedule);
        
    }
    
    /**
     * @Route("/schedule/edit/{id}", name="schedule_edit", requirements={
     *     "id": "\d+"
     * })
     * @Method({"POST", "PUT"})
     */
    public function editAction(Request $request, $id){
        
        $em = $this->getDoctrine()->getManager();
        $schedule = $em->getRepository('AppBundle:Schedule')->find($id);
    
        if (!$schedule) {
            $mess = "schedule(id=$id) not found !";
            return new ServiceResponse(null, false, $mess);
        }
        
        if ($request->request->get("start_time") != null) {
            $schedule->setStartTime(new \DateTime($request->request->get("start_time")));
        }
        else{
            $schedule->setStartTime(null);
        }
        
        if ($request->request->get("end_time") != null) {
            $schedule->setEndTime(new \DateTime($request->request->get("end_time")));
        }
        else{
            $schedule->setEndTime(null);
        }
        
        $schedule->setMessage($request->request->get("message"));
        
        $validator = $this->get('validator');
        $errors = $validator->validate($schedule);
        
        if (count($errors) > 0) {
            
            $mess = ServiceResponse::messageFromValidateErrorList($errors);
            
            return new ServiceResponse($schedule, false, $mess);
        }
        
        $em->flush();
        
        return new ServiceResponse($schedule);
              
    }

    /**
     * @Route("/schedule/delete/{id}", name="schedule_delete", requirements={
     *     "id": "\d+"
     * })
     * @Method({"POST", "DELETE"})
     */
    public function deleteAction(Request $request, $id){
        
        $em = $this->getDoctrine()->getManager();
        
        /**
         * @var Schedule
         */
        $schedule = $em->getRepository('AppBundle:Schedule')->find($id);
    
        if (!$schedule) {
            $mess = "schedule(id=$id) not found !";
            return new ServiceResponse(null, false, $mess);
        }
    
        $schedule->setDelFlag(true);
        
        $em->flush();
        
        return new ServiceResponse($schedule);
              
    }
    
    /**
     * @Route("/schedule/undelete/{id}", name="schedule_undelete", requirements={
     *     "id": "\d+"
     * })
     * @Method({"POST", "PUT"})
     */
    public function unDeleteAction(Request $request, $id){
        
        $em = $this->getDoctrine()->getManager();
        
        /**
         * @var Schedule
         */
        $schedule = $em->getRepository('AppBundle:Schedule')->find($id);
    
        if (!$schedule) {
            $mess = "schedule(id=$id) not found !";
            return new ServiceResponse(null, false, $mess);
        }
    
        $schedule->setDelFlag(false);
        
        $validator = $this->get('validator');
        $errors = $validator->validate($schedule);
        
        if (count($errors) > 0) {
            
            $mess = ServiceResponse::messageFromValidateErrorList($errors);
            
            return new ServiceResponse($schedule, false, $mess);
        }
        
        $em->flush();
        
        return new ServiceResponse($schedule);
              
    }
}