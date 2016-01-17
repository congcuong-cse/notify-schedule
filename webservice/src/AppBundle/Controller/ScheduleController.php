<?php

namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\JsonResponse;

class ScheduleController extends Controller
{
    /**
	 * @Route("/schedules", name="schedule_all")
     */
    public function allAction()
    {
    	$repository = $this->getDoctrine()
			->getRepository('AppBundle:Schedule');
			
		$schedules = $repository->findAll();
		
        $response = new JsonResponse();
		$response->setData($schedules);
		
		return $response;
    }
}