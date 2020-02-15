#!/usr/bin/env python
import sys
import os
import urllib
import base64
import hmac
import hashlib
from hashlib import sha1

D = {
    'accessKeyId': 'testid',
    'uidKey': '0802',
    'signatureNonce': '53c593e7-766d-4646-8b58-0b795ded0ed6',
    'timestamp': '2019-10-10T08:26:01Z'
}
sortedD = sorted(D.items(), key=lambda x: x[0])
canstring = ''

for k, v in sortedD:
    canstring += '&' + k + '=' + v

print(canstring[1:])

access_key_secret = 'testsecret'
h = hmac.new(access_key_secret + "&", canstring[1:], sha1)

signature = base64.encodestring(h.digest()).strip()
print(signature)