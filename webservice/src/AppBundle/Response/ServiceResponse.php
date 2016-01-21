<?php 

namespace AppBundle\Response;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Validator\ConstraintViolationListInterface;
use Symfony\Component\Validator\ConstraintViolationInterface;

/**
 * Service Response
 */
class ServiceResponse extends Response {
    
    // Encode <, >, ', &, and " for RFC4627-compliant JSON, which may also be embedded into HTML.
    // 15 === JSON_HEX_TAG | JSON_HEX_APOS | JSON_HEX_AMP | JSON_HEX_QUOT
    protected $encodingOptions = 15;
    
    /**
     * @var bool
     */
    private $success = true;
    
    /**
     * @var string
     */
    private $code = "";
    
    /**
     * @var string
     */
    private $message = "";
    
    /**
     * construct
     *
     * @param array     $data
     * @param message   $message
     * @param bool      $success
     * @param array     $errors
     */
	function __construct($data = array(), $success = true, $message="", $code = "") {
	    parent::__construct('', 200, array('Content-Type'=> 'application/json'));
        
        $this->code = $code;
        $this->success = $success;
        $this->message = $message;
        $this->data = $data;
        
        $this->update();
	}
    
    protected function update(){
        $data = 
          array(
                  "success" => $this->success,
                  "code"  => $this->code,
                  "message" => $this->message,
                  "data"    => $this->data
            );
            
        if (defined('HHVM_VERSION')) {
            // HHVM does not trigger any warnings and let exceptions
            // thrown from a JsonSerializable object pass through.
            // If only PHP did the same...
            $data = json_encode($data, $this->encodingOptions);
        } else {
            try {
                if (PHP_VERSION_ID < 50400) {
                    // PHP 5.3 triggers annoying warnings for some
                    // types that can't be serialized as JSON (INF, resources, etc.)
                    // but doesn't provide the JsonSerializable interface.
                    set_error_handler(function () { return false; });
                    $data = @json_encode($data, $this->encodingOptions);
                } else {
                    // PHP 5.4 and up wrap exceptions thrown by JsonSerializable
                    // objects in a new exception that needs to be removed.
                    // Fortunately, PHP 5.5 and up do not trigger any warning anymore.
                    if (PHP_VERSION_ID < 50500) {
                        // Clear json_last_error()
                        json_encode(null);
                        $errorHandler = set_error_handler('var_dump');
                        restore_error_handler();
                        set_error_handler(function () use ($errorHandler) {
                            if (JSON_ERROR_NONE === json_last_error()) {
                                return $errorHandler && false !== call_user_func_array($errorHandler, func_get_args());
                            }
                        });
                    }

                    $data = json_encode($data, $this->encodingOptions);
                }

                if (PHP_VERSION_ID < 50500) {
                    restore_error_handler();
                }
            } catch (\Exception $e) {
                if (PHP_VERSION_ID < 50500) {
                    restore_error_handler();
                }
                if (PHP_VERSION_ID >= 50400 && 'Exception' === get_class($e) && 0 === strpos($e->getMessage(), 'Failed calling ')) {
                    throw $e->getPrevious() ?: $e;
                }
                throw $e;
            }
        }

        if (JSON_ERROR_NONE !== json_last_error()) {
            throw new \InvalidArgumentException(json_last_error_msg());
        }
            
        $this->setContent($data);
    }
    
    /**
     * @param ConstraintViolationListInterface $error_list
     * @param string $sperator
     * 
     * @return string
     */
    public static function messageFromValidateErrorList(ConstraintViolationListInterface $error_list, $sperator = PHP_EOL){
        $mess_arr = array();
        
        foreach ($error_list as $key => $violate) {
            /* @var $violate ConstraintViolationInterface  */
            $mess_arr[] = $violate->getPropertyPath() . ": " . $violate->getMessage(); 
        }        
            
        return implode($sperator, $mess_arr);
    }
    
    
}

 ?>