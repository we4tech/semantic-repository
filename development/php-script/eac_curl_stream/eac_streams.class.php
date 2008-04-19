<?php/** * eac_streams.class.php * curl class * * PHP version 5 * * @author	   Kevin Burkholder <KBurkholder@EarthAsylum.com> *//* +------------------------------------------------------------------------+   | Copyright 2008, Kevin Burkholder               www.KevinBurkholder.com |   | Some rights reserved.                                                  |   |                                                                        |   | This work is licensed under the Creative Commons GNU Lesser General    |   | Public License. To view a copy of this license, visit                  |   |     http://creativecommons.org/licenses/LGPL/2.1/                      |   |                                                                        |   | Please see the License_LGPL_x.x.txt file for redistribution and use    |   | restrictions. If this file was not included with the distribution of   |   | this software, it may be found here:                                   |   |     http://www.kevinburkholder.com/sw_license.php                      |   |                                                                        |   | THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    |   | "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      |   | LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR  |   | A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT   |   | OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  |   | SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT       |   | LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  |   | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  |   | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT    |   | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE  |   | OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.   |   |                                                                        |   +------------------------------------------------------------------------+   |                                                                        |   | Author:     Kevin Burkholder                                           |   |             EarthAsylum Consulting                                     |   |             KBurkholder@EarthAsylum.com                                |   |                                                                        |   +------------------------------------------------------------------------+ */ /** a simple GET request: * include('eac_streams.class.php'); * $http = new stream(); * $result = $http->get('http://www.kevinburkholder.com/PostTest.php'); */ /** * // set some fields * $fields = array(); * $fields['FirstName'] = "Kevin"; * $fields['LastName'] 	= "Burkholder"; * $fields['FullName'] 	= "Kevin J. Burkholder"; * $fields['Email']		= "kburkholder@earthasylum.com"; *  * // set some options * $options = array(); * $options['STREAMS_MAXREDIRS']	= 5; * $options['STREAMS_TIMEOUT'] 		= 60; * $options['STREAMS_ENCODING'] 	= null; *  * include('eac_streams.class.php'); * // instantiate and load options * $http = new stream($options); * // another way to load options * $http->setOptions($options); *  * // add a new (single) option * $http->setOption('STREAMS_USERPWD', "user:password"); *  * // copy the http request headers to the stream request * $http->copyHeaders(); *  * // add my own header * $http->header('X-EAC-STREAM-Test: stream test header'); *  * // post to PostTest.php with fields * echo $http->post('http://www.kevinburkholder.com/PostTest.php',$fields); *  * // display the response headers, the stream stats, and our options * echo "<pre>"; * print_r($http->getHeaders()); * print_r($http->getInfo()); * print_r($http->getOptions()); * echo "</pre>"; * * // $http->success = false on error, else true * // $http->error = error message (on error) */ class stream {	var $Version		= "v0.3.1, (Feb 10, 2008)";	var $Signature		= "eac_streams.class.php; %s; [www.KevinBurkholder.com]";	var $Type			= "STREAM";	/**	 * Optional callback function	 *	 * @var mixed	 * @access private	 */	private $callback = false;	/**	 * request headers	 *	 * @var array	 * @access private	 */	private $reqHeaders = array();	/**	 * response headers	 *	 * @var array	 * @access private	 */	private $resHeaders = array();	/**	 * response info	 *	 * @var array	 * @access private	 */	private $info = array();	/**	 * streams options	 *	 * @var array	 * @access private	 */	private $options = array();	/**	 * streams parameters	 *	 * @var array	 * @access private	 */	private $params = array();	/**	 * streams result	 *	 * @var string	 * @access private	 */	private $lastResult = null;	/**	 * streams status	 *	 * @var string	 * @access private	 */	public $success = null;	public $error = null;	/**	 * Constrtuctor method	 *	 * @param array $options	curl_setopt options	 */	public function __construct($options = null)	{		$this->Signature = sprintf($this->Signature,$this->Version);		$this->setOptions($options);	}	/**	 * set options	 *	 * @param array $options	option = value	 */	public function setOptions($options = null)	{		$this->reqHeaders = array();		$this->options['STREAMS_HEADER']			= 0;							// no headers in result		$this->options['STREAMS_USERAGENT']			= $_SERVER['HTTP_USER_AGENT'];	// pass user agent		$this->options['STREAMS_MAXREDIRS']			= 10;							// max redirects		$this->options['STREAMS_TIMEOUT']			= 120;							// max time in seconds		$this->options['STREAMS_ENCODING']			= "gzip";						// accept gzip encoding		$this->options['STREAMS_RETURNTRANSFER']	= 1;							// return results as string		$this->options['STREAMS_SSL_VERIFYPEER']	= 0;							// don't verify ssl certs		$this->options['STREAMS_USERPWD']			= null;							// user:password		$this->options['STREAMS_ASYNCRONOUS']		= 0;							// wait for/read results		if (is_array($options)) {			foreach($options as $opt => $val) $this->setOption($opt,$val);		}	}		/**	 * set a single option	 *	 * @param string $option	option name	 * @param mixed $value		value	 */	public function setOption($option, $value)	{		$option = str_replace('CURLOPT','STREAMS',$option);		if (isset($this->options[$option])) $this->options[$option] = $value;	}			/**	 * set a callback function	 * myfunction(stream_result [, curl_handle=null]) {}	 *	 * @access public	 * @param string $function	'function_name'	 * @param array $function	array('class_name', 'function_name')	 */	public function setCallback($function) 	{		$this->callback = $function;	}	/**	 * set default headers	 *	 * @access public	 */	public function copyHeaders() 	{		foreach($_SERVER as $key => $value) {			if (substr($key,0,5) == "HTTP_") {				if (substr($key,5,1) != "X")					$key = str_replace(' ','-',ucwords(strtolower(str_replace('_',' ',str_replace('HTTP_','',$key)))));				else					$key = str_replace('_','-',str_replace('HTTP_','',$key));				if (strtolower($key) != "cookie" && strtolower($key) != "accept-encoding")					$this->header($key.": ".$value);			}		}	}		/**	 * add a request header	 *	 * @access public	 * @param string $header	header (header-name: header-value)	 */	public function header($header) 	{		if (!in_array($header,$this->reqHeaders))			$this->reqHeaders[] = $header;	}		/**	 * http GET request	 *	 * @access public	 * @param string $url 		the request url	 * @param array 			$options additional options	 */	public function get($url, $options = null) 	{		$this->_resetOpt();		if (is_array($options)) 			foreach($options as $opt => $val) $this->setOption($opt,$val);		$this->params = array('http' => array('method' => 'GET'));		return $this->httpRequest($url);	}		/**	 * http POST request	 *	 * @access public	 * @param string $url 		the request url	 * @param mixed $fields 	POST variables	 * @param array $options 	additional options	 */	public function post($url, $fields = null, $options = null) 	{		$this->_resetOpt();		if (is_array($fields)) {			foreach($fields as $k => $v) $vars .= $k.'='.utf8_encode($v)."&";			$fields = substr($vars,0,-1);		}		$this->header('Content-Type: application/x-www-form-urlencoded');		$this->params = array('http' => array('method' => 'POST','content' => $fields));		return $this->httpRequest($url);	}		/**	 * http PUT request	 *	 * @access public	 * @param string $url 		the request url	 * @param string $fn_or_data '@filename' or data string	 * @param array $options 	additional options	 */	public function put($url, $fn_or_data, $options = null) 	{		$this->_resetOpt();		if (is_array($options)) 			foreach($options as $opt => $val) $this->setOption($opt,$val);		if (substr($fn_or_data,0,1) == '@') {			$fn_or_data = substr($fn_or_data,1);			$data = @file_get_contents($fn_or_data,FILE_USE_INCLUDE_PATH);			if (!$data) {				$fn_or_data = str_replace('//','/',$_SERVER['DOCUMENT_ROOT']."/".$fn_or_data);				$data = @file_get_contents($fn_or_data,FILE_USE_INCLUDE_PATH);			}		} else $data = $fn_or_data;				if (!isset($this->options['STREAMS_USERPWD']) && isset($_SERVER['SERVER_ADMIN']))			$this->options['STREAMS_USERPWD'] = "anonymous:".$_SERVER['SERVER_ADMIN'];		$this->params = array('http' => array('method' => 'PUT','content' => $data));		return $this->httpRequest($url);	}	/**	 * http request	 *	 * @access public	 * @param string $url 		the request url	 */	public function httpRequest($url) 	{		$this->header('X-EAC-Request: '.$this->Signature);		$url = str_replace('&amp;','&',$url);		foreach($this->options as $opt => $val) {			switch ($opt) {				case 'STREAMS_SSL_VERIFYPEER':					$this->params['http']['verify_peer'] = $val;					$this->params['http']['allow_self_signed'] = !$val;					break;				case 'STREAMS_USERAGENT':					if ($val) $this->params['http']['user_agent'] = $val;					break;				case 'STREAMS_MAXREDIRS':					$this->params['http']['max_redirects'] = $val;					break;				case 'STREAMS_TIMEOUT':					$this->params['http']['timeout'] = $val;					break;				case 'STREAMS_ENCODING':					if ($val) $this->header("Accept-Encoding: $val");					break;				case 'STREAMS_USERPWD':					if ($val) {						list($prot,$uri) = explode("://",$url,2);						$url = $prot."://".urlencode($val)."@".$uri;					}					break;			}		}				if (count($this->reqHeaders) > 0) {			$this->params['http']['header'] = $this->reqHeaders;		}		$ctx = stream_context_create($this->params);		$fp = @fopen($url, 'rb', false, $ctx);				if ($fp) {			if ($this->options['STREAMS_ASYNCRONOUS']) {				$this->success = true;				$this->lastResult = "OK";				return $this->lastResult;			}			$this->lastResult = @stream_get_contents($fp);		}				if ($this->lastResult) {			$this->_parseHeaders($http_response_header,$url);			if (stripos($this->resHeaders['Content-Encoding'],"gzip") !== false) {				$this->lastResult = $this->_gzdecode($this->lastResult);				$this->info['decoded_content_length'] = strlen($this->lastResult);			}			if ($this->options['STREAMS_HEADER']) {				$headers = "";				foreach($this->resHeaders as $k => $v) $headers .= $k.": ".$v."\r\n";				$this->lastResult = $headers."\r\n".$this->lastResult;			}			if ($this->callback) {				$this->lastResult = call_user_func($this->callback, $this->lastResult);				$this->callback = false;			}			$this->success = true;		} else {			$this->success = false;			$this->error = $php_errormsg;			$this->lastResult = $this->error;		}		if (!$this->options['STREAMS_RETURNTRANSFER']) {			echo $this->lastResult;			return $this->success;		}		return $this->lastResult;	}		/**	 * send (email) last result	 *	 * @access public	 * @param string $to 		the 'to' email address	 * @param string $from 		the 'from' email address	 * @param string $subject 	the email subject	 * @param string $xheaders 	extra headers	 * @param string $EOL	 	end-of-line character (\n or \r\n)	 */	public function sendLastResult($to, $from = false, $subject = false, $xheaders = array(), $EOL = "\n") 	{		if (!$from && isset($_SERVER['SERVER_ADMIN'])) {			$from = $_SERVER['SERVER_ADMIN'];		}		if (!$subject) {			$subject = array_shift(explode(';',$this->Signature))." Results";		}		if (is_string($xheaders)) {			$xheaders = explode(";",$xheaders);		}		$headers  = "X-Mailer: " . $this->Signature . $EOL;		$headers .=	"From: " . $from . $EOL;		if (isset($this->info['content_type'])) {			$headers .= "Content-Type: ".$this->info['content_type'] . $EOL;		}		if (is_array($xheaders)) {			foreach ($xheaders as $header) $headers .= $header . $EOL;		}		return mail($to, $subject, $this->lastResult, $headers);	}		/**	 * get last result	 *	 * @access public	 */	public function getLastResult() 	{		return $this->lastResult;	}	/**	 * get response info	 *	 * @access public	 */	public function getInfo() 	{		return $this->info;	}		/**	 * get response headers	 *	 * @access public	 */	public function getHeaders() 	{		return $this->resHeaders;	}		/**	 * get options array	 *	 * @access public	 */	public function getOptions() 	{		return $this->options;	}	/**	 * callback function for reading and processing headers	 * 	 * @return integer		size of headers	 */	private function _parseHeaders($response_header, $url) 	{		$hdrsize = 0;		$rdrcount = 0;		foreach($response_header as $k => $header) {			list($key,$value) = explode(" ",rtrim($header,"\r\n"),2);			if (substr($key,0,1) != "X")				$key = str_replace(' ','-',ucwords(strtolower(str_replace('-',' ',$key))));			$hdrsize += strlen($key." ".$value);			$key = str_replace('Http','HTTP',rtrim($key,":"));			$this->resHeaders[$key] = $value;			if ($key == "Location") {				$rdrcount +=1;				$url = $value;			}		}		$this->info = array();		$this->info['url'] 				= $url;		$this->info['content_type'] 	= $this->resHeaders['Content-Type'];		$this->info['header_size'] 		= $hdrsize;		$this->info['http_code'] 		= array_shift(explode(' ',$this->resHeaders['HTTP/1.1'].$this->resHeaders['HTTP/1.0']));		$this->info['redirect_count']	= $rdrcount;		$this->info['size_download'] 	= $this->resHeaders['Content-Length'];		$this->info['download_content_length'] 	= $this->resHeaders['Content-Length'];    	return $hdrsize;	}	/**	 * unset function parameters	 * 	 */	private function _resetOpt() 	{		$this->params = array();		$this->info = array();		$this->resHeaders = array();			}	/**	 * gzdecode function (missing from PHP)	 * 	 * @param string $data 		gzencoded data	 * @return string decoded data	 */	private function _gzdecode($data) {		$flags = ord(substr($data, 3, 1));		$headerlen = 10;		$extralen = 0;		$filenamelen = 0;		if ($flags & 4) {			$extralen = unpack('v' ,substr($data, 10, 2));			$extralen = $extralen[1];			$headerlen += 2 + $extralen;		}		if ($flags & 8) // Filename			$headerlen = strpos($data, chr(0), $headerlen) + 1;		if ($flags & 16) // Comment			$headerlen = strpos($data, chr(0), $headerlen) + 1;		if ($flags & 2) // CRC at end of file			$headerlen += 2;		$unpacked = gzinflate(substr($data, $headerlen));		if ($unpacked === false) $unpacked = $data;		return $unpacked;	}}?>