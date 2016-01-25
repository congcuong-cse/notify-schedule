<?php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use JsonSerializable;

/**
 * Schedule
 *
 * @ORM\Table(name="schedule")
 * @ORM\Entity(repositoryClass="AppBundle\Repository\ScheduleRepository")
 */
class Schedule implements JsonSerializable
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="start_time", type="time")
	 * @Assert\NotBlank()
     */
    private $startTime;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="end_time", type="time")
	 * @Assert\NotBlank()
     */
    private $endTime;

    /**
     * @var string
     *
     * @ORM\Column(name="message", type="text")
     */
    private $message;

    /**
     * @var bool
     *
     * @ORM\Column(name="del_flag", type="boolean", options={"default" = false})
     */
    private $delFlag = false;


    /**
     * Get id
     *
     * @return int
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set startTime
     *
     * @param \DateTime $startTime
     *
     * @return Schedule
     */
    public function setStartTime($startTime)
    {
        $this->startTime = $startTime;

        return $this;
    }

    /**
     * Get startTime
     *
     * @return \DateTime
     */
    public function getStartTime()
    {
        return $this->startTime;
    }

    /**
     * Set endTime
     *
     * @param \DateTime $endTime
     *
     * @return Schedule
     */
    public function setEndTime($endTime)
    {
        $this->endTime = $endTime;

        return $this;
    }

    /**
     * Get endTime
     *
     * @return \DateTime
     */
    public function getEndTime()
    {
        return $this->endTime;
    }


    /**
     * Set message
     *
     * @param string $message
     *
     * @return Schedule
     */
    public function setMessage($message)
    {
        $this->message = $message;

        return $this;
    }

    /**
     * Get message
     *
     * @return string
     */
    public function getMessage()
    {
        return $this->message;
    }

    /**
     * Set delFlag
     *
     * @param boolean $delFlag
     *
     * @return Schedule
     */
    public function setDelFlag($delFlag)
    {
        $this->delFlag = $delFlag;

        return $this;
    }

    /**
     * Get delFlag
     *
     * @return bool
     */
    public function getDelFlag()
    {
        return $this->delFlag;
    }
	
    /**
     * implement JsonSeriable
     * 
     * @return string
     */
	public function jsonSerialize()
    {
        return array(
            'id' => $this->getId(),
            'start_time'=> $this->getStartTime() == null ? null : $this->getStartTime()->format('H:i'),
            'end_time' => $this->getEndTime() == null ? null : $this->getEndTime()->format('H:i'),
			'message' => $this->getMessage(),
			'del_flag' => $this->getDelFlag(),
        );
    }
}
