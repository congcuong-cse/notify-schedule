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
		
		return new JsonResponse($schedules);
    }
    
    /**
     * @Route("/schedules/create", name="schedule_create")
     * @Method({"POST"})
     */
    public function createAction(Request $request){
        $schedule = new Schedule();
        $schedule->setStartTime(new \DateTime($request->request->get("start_time")));
        
        $validator = $this->get('validator');
        $errors = $validator->validate($schedule);
        
        if (count($errors) > 0) {
            
            $mess = ServiceResponse::messageFromValidateErrorList($errors);
            
            return new ServiceResponse($schedule, false, $mess);
        }
        
        return new ServiceResponse($schedule);
        
    }
}