import json
 
import requests
 
 
def lambda_handler(event, context):
    """Sample pure Lambda function
    Arguments:
        event LambdaEvent -- Lambda Event received from Invoke API
        context LambdaContext -- Lambda Context runtime methods and attributes
    Returns:
        dict -- {'statusCode': int, 'body': dict}
    """
 
    ip = requests.get('http://checkip.amazonaws.com/')
 
    try:
        if 1 == 1:
            if 2 == 2:
                if 3 == 3:
                    if 4 == 4:
                        if 5 == 5:
                            print(1)
                        else:
                            pass
                    else:
                        pass
                else:
                    pass
            else:
                pass
        else:
            pass                               
    except Exception as e:
        pass
 
    return {
        "statusCode": 200,
        "body": json.dumps({
            'message': 'hello world',
            'location': ip.text.replace('\n', ''),
        })
}
