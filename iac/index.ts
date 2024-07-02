import * as pulumi from "@pulumi/pulumi";
import * as aws from "@pulumi/aws";
import {RolePolicyAttachment} from "@pulumi/aws/iam";

const defaultRole = new aws.iam.Role("habitpal-default-role", {
    assumeRolePolicy: `{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
`
});

new RolePolicyAttachment("habitpal-default-role-policy",
    {
        role: defaultRole,
        policyArn: aws.iam.ManagedPolicies.AWSLambdaBasicExecutionRole
    });

const lambdaFunction = new aws.lambda.Function("habitpal", {
    code: new pulumi.asset.FileArchive("../build/distributions/habitpal-1.0-SNAPSHOT.zip"),
    handler: "uk.co.kiteframe.hapitpal.ServerlessHabitPal",
    role: defaultRole.arn,
    runtime: "java21",
    memorySize: 256,
    timeout: 15
});

const logGroupApi = new aws.cloudwatch.LogGroup("habitpal-api-route", {
    name: "habitpal",
});

const apiGatewayPermission = new aws.lambda.Permission("habitpal-gateway-permission", {
    action: "lambda:InvokeFunction",
    "function": lambdaFunction.name,
    principal: "apigateway.amazonaws.com"
});

const api = new aws.apigatewayv2.Api("habitpal-api", {
    protocolType: "HTTP"
});

const apiDefaultStage = new aws.apigatewayv2.Stage("default", {
    apiId: api.id,
    autoDeploy: true,
    name: "$default",
    accessLogSettings: {
        destinationArn: logGroupApi.arn,
        format: `{"requestId": "$context.requestId", "requestTime": "$context.requestTime", "httpMethod": "$context.httpMethod", "httpPath": "$context.path", "status": "$context.status", "integrationError": "$context.integrationErrorMessage"}`
    }
})

const lambdaIntegration = new aws.apigatewayv2.Integration("habitpal-api-lambda-integration", {
    apiId: api.id,
    integrationType: "AWS_PROXY",
    integrationUri: lambdaFunction.arn,
    payloadFormatVersion: "2.0"
});

let serverlessHttp4kApiRoute = "habitpal";
const apiDefaultRole = new aws.apigatewayv2.Route(serverlessHttp4kApiRoute + "-api-route", {
    apiId: api.id,
    routeKey: `$default`,
    target: pulumi.interpolate `integrations/${lambdaIntegration.id}`
});

export const publishedUrl = apiDefaultStage.invokeUrl;