USE [FNILP]
GO

/****** Object:  Table [dbo].[SECURITYUSER]    Script Date: 03-Sep-20 10:00:28 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[SECURITYUSER](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](50) NULL,
	[email] [varchar](50) NULL,
	[password] [varchar](200) NULL
) ON [PRIMARY]
GO

------------------------------------------------------------------------------------------------------------
USE [FNILP]
GO

/****** Object:  Table [dbo].[SECURITYUSER_ROLE]    Script Date: 03-Sep-20 10:01:58 AM ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[SECURITYUSER_ROLE](
	[roles] [varchar](100) NULL,
	[USERID] [int] NULL
) ON [PRIMARY]
GO

------------------------------------------------------------------------------------------------------------
USE [FNILP]
GO
SET IDENTITY_INSERT [dbo].[SECURITYUSER] ON 
GO
INSERT [dbo].[SECURITYUSER] ([id], [username], [email], [password]) VALUES (1, N'string', N'string', N'$2a$10$Z5IwCRpIcQoLLgE/bPNXzug9iZixgVBCmmLNzlszguR/snDTDe1di')
GO
INSERT [dbo].[SECURITYUSER] ([id], [username], [email], [password]) VALUES (2, N'admin', N'admin', N'$2a$10$g8Om5lr8KCM3TW4ARjBjVef9SKBHtVnDLbz7aqlTNqDrjnoG/aLAK')
GO
SET IDENTITY_INSERT [dbo].[SECURITYUSER] OFF
GO
INSERT [dbo].[SECURITYUSER_ROLE] ([roles], [USERID]) VALUES (N'0', 1)
GO
INSERT [dbo].[SECURITYUSER_ROLE] ([roles], [USERID]) VALUES (N'0', 2)
GO