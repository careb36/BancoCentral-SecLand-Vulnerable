// Health check endpoint for Docker container
export async function GET() {
  return Response.json({ 
    status: "healthy", 
    timestamp: new Date().toISOString(),
    service: "central-bank-frontend"
  })
}
